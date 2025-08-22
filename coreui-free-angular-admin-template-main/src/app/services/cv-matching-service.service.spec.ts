import { TestBed } from '@angular/core/testing';

import { CvMatchingServiceService } from './cv-matching-service.service';

describe('CvMatchingServiceService', () => {
  let service: CvMatchingServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CvMatchingServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
