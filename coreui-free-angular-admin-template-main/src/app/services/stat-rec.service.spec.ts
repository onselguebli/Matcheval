import { TestBed } from '@angular/core/testing';

import { StatRecService } from './stat-rec.service';

describe('StatRecService', () => {
  let service: StatRecService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StatRecService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
