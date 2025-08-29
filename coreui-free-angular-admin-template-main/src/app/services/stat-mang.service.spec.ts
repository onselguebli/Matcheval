import { TestBed } from '@angular/core/testing';

import { StatMangService } from './stat-mang.service';

describe('StatMangService', () => {
  let service: StatMangService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StatMangService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
