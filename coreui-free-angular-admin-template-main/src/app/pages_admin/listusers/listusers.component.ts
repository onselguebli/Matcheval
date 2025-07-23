import { Component } from '@angular/core';
import { TableDirective } from '@coreui/angular';
import { User } from '../../models/User';
import { AdminService } from '../../services/admin.service';
import { CommonModule } from '@angular/common';
import { UpdateuserComponent } from '../updateuser/updateuser.component';
import { Pagination1Component } from "../../pagination-1/pagination-1.component";
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-listusers',
  imports: [TableDirective, CommonModule, UpdateuserComponent, Pagination1Component, FormsModule],
  templateUrl: './listusers.component.html',
  styleUrl: './listusers.component.scss'
})
export class ListusersComponent {


  users: User[] = [];
  selectedUserId: number | null = null;
  currentPage = 1;
  itemsPerPage = 13;
  searchEmail: string = '';
  filteredUsers: User[] = [];
  selectedManagerId: number | null = null;
  recruteursOfManager: User[] = [];
 usersByRole: { [role: string]: User[] } = {};
  roles: string[] = [];

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
   this.loadUsers();
}

showUpdateForm(userId: number): void {
  this.selectedUserId = userId;
}

closeUpdateForm(): void {
  this.selectedUserId = null;
}

loadUsers(): void {
  this.adminService.getAllUsers().subscribe({
    next: data => {
      this.users = data.listusers;
      this.filteredUsers = this.users;
      this.usersByRole = this.getUsersByRole();
      this.roles = Object.keys(this.usersByRole);
    },
    error: err => console.error('Erreur chargement utilisateurs', err)
  });
}
handleFormClosed(updated: boolean): void {
  this.selectedUserId = null;
  if (updated) {
    this.loadUsers(); }
}

toggleActivation(user: User): void {
  this.adminService.block(user.id).subscribe({
    next: () => this.loadUsers(), 
    error: err => console.error(' Erreur mise à jour statut', err)
  });
}

get paginatedUsers(): User[] {
  const list = this.filteredUsers.length > 0 || this.searchEmail ? this.filteredUsers : this.users;
  const start = (this.currentPage - 1) * this.itemsPerPage;
  return list.slice(start, start + this.itemsPerPage);
}
filterUsers(): void {
  const search = this.searchEmail.toLowerCase();
  this.filteredUsers = this.users.filter(user =>
    user.email?.toLowerCase().includes(search)
  );
  this.currentPage = 1; // revenir à la première page si on filtre
}

onPageChange(page: number): void {
  this.currentPage = page;
}
showRecruteurs(managerId: number): void {
  if (this.selectedManagerId === managerId) {
    
    this.selectedManagerId = null;
    this.recruteursOfManager = [];
  } else {
   
    this.selectedManagerId = managerId;
    this.adminService.getRecruteursByManager(managerId).subscribe({
      next: data => {
        this.recruteursOfManager = data;
      },
      error: err => {
        console.error('Erreur chargement recruteurs', err);
      }
    });
  }
}

getUsersByRole(): { [role: string]: User[] } {
  const list = this.filteredUsers.length > 0 || this.searchEmail ? this.filteredUsers : this.users;
  return list.reduce((acc, user) => {
    if (!acc[user.role]) {
      acc[user.role] = [];
    }
    acc[user.role].push(user);
    return acc;
  }, {} as { [role: string]: User[] });
}

}
