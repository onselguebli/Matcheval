import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CvMatchingComponent } from './cv-matching.component';

describe('CvMatchingComponent', () => {
  let component: CvMatchingComponent;
  let fixture: ComponentFixture<CvMatchingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CvMatchingComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CvMatchingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
