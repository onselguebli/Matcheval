// src/app/models/meeting.ts
export interface Meeting {
  id?: number;
  title: string;
  roomName: string;
  startAt?: string; // ISO
  durationMin?: number;
  createdBy?: string;
  inviteLink?: string;
  password?: string;
}
