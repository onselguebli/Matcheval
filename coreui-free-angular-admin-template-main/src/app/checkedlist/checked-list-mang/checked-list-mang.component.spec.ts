import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CheckedListMangComponent } from './checked-list-mang.component';

describe('CheckedListMangComponent', () => {
  let component: CheckedListMangComponent;
  let fixture: ComponentFixture<CheckedListMangComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CheckedListMangComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CheckedListMangComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
