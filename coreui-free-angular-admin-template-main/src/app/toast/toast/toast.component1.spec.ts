import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ToastComponent1 } from './toast.component1';

describe('ToastComponent', () => {
  let component: ToastComponent1;
  let fixture: ComponentFixture<ToastComponent1>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ToastComponent1]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ToastComponent1);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
