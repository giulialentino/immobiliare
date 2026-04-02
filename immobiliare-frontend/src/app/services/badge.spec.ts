import { TestBed } from '@angular/core/testing';

import { Badge } from './badge';

describe('Badge', () => {
  let service: Badge;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Badge);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
