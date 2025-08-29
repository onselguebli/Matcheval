import { Component, AfterViewInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MeetingServiceService as MeetingService } from '../../services/meeting-service.service';
import { Meeting } from '../../models/Meeting';

declare var JitsiMeetExternalAPI: any;
@Component({
  selector: 'app-meeting-room',
  imports: [CommonModule, FormsModule],
  templateUrl: './meeting-room.component.html',
  styleUrl: './meeting-room.component.scss'
})
export class MeetingRoomComponent implements AfterViewInit, OnDestroy{
meetings: Meeting[] = [];
  form: Partial<Meeting> = { title: '', durationMin: 30 };
  loading = false;
  error: string | null = null;
  success: string | null = null;

  domain = 'meet.jit.si'; // ou ton domaine Jitsi
  api: any = null;        // instance Jitsi

  constructor(private meetingSvc: MeetingService) {}

  ngAfterViewInit(): void {
    this.load();
  }
  ngOnDestroy(): void {
    if (this.api) this.api.dispose();
  }

  load() {
    this.loading = true;
    this.meetingSvc.listMine().subscribe({
      next: (arr) => { this.meetings = arr; this.loading = false; },
      error: () => { this.error = 'Erreur chargement réunions'; this.loading = false; }
    });
  }

  // Génère un nom de salle robuste
  private generateRoomName(title: string) {
    const slug = (title || 'reunion').toLowerCase().replace(/[^a-z0-9]+/g,'-');
    const rand = Math.random().toString(36).slice(2,8);
    return `room-${slug}-${rand}`;
  }

  createMeeting() {
    if (!this.form.title) { this.error = 'Titre requis'; return; }
    const roomName = this.generateRoomName(this.form.title);
    const startAt = this.form.startAt || new Date().toISOString();

    this.meetingSvc.create({
      title: this.form.title!,
      roomName,
      startAt,
      durationMin: this.form.durationMin || 30,
      password: this.form.password
    }).subscribe({
      next: (m) => {
        this.success = 'Réunion créée';
        this.meetings = [m, ...this.meetings];
        this.join(m);
      },
      error: () => this.error = 'Erreur création réunion'
    });
  }

  join(m: Meeting) {
    // détruit l’instance précédente si existante
    if (this.api) { this.api.dispose(); this.api = null; }

    const options = {
      roomName: m.roomName,
      parentNode: document.querySelector('#jitsi-container'),
      width: '100%',
      height: 600,
      configOverwrite: {
        prejoinPageEnabled: true,
        startWithAudioMuted: true,
        disableThirdPartyRequests: true
      },
      interfaceConfigOverwrite: {
        DISABLE_JOIN_LEAVE_NOTIFICATIONS: false,
      },
      userInfo: { displayName: 'Manager' } // tu peux mettre le nom du user
    };

    this.api = new JitsiMeetExternalAPI(this.domain, options);

    // mot de passe (si défini & si tu as les droits de modération)
    if (m.password) {
      this.api.addEventListener('videoConferenceJoined', () => {
        this.api.executeCommand('password', m.password);
      });
    }

    // raccourcis utiles côté manager :
    // this.api.executeCommand('toggleShareScreen');
    // this.api.executeCommand('startRecording', { mode: 'file' }); // nécessite Jibri
  }
}
