import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { PageItemDirective, PaginationComponent } from "@coreui/angular";

@Component({
  selector: 'app-pagination-1',
  imports: [PaginationComponent, CommonModule,PageItemDirective,],
  templateUrl: './pagination-1.component.html',
  styleUrl: './pagination-1.component.scss'
})
export class Pagination1Component {
 @Input() currentPage: number = 1;
  @Input() totalItems: number = 0;
  @Input() itemsPerPage: number = 5;
  @Output() pageChanged = new EventEmitter<number>();

  get totalPages(): number {
    return Math.ceil(this.totalItems / this.itemsPerPage);
  }

  changePage(page: number): void {
  if (page >= 1 && page <= this.totalPages) {
    this.pageChanged.emit(page); // ✅ emits a number
  }
}

  pages(): number[] {
    return Array(this.totalPages).fill(0).map((_, i) => i + 1);
  }
}
