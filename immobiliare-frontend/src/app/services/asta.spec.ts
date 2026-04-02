import { TestBed } from '@angular/core/testing';

import { Asta } from './asta';

describe('Asta', () => {
  let service: Asta;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Asta);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
