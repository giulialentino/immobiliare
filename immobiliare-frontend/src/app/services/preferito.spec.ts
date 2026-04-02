import { TestBed } from '@angular/core/testing';

import { Preferito } from './preferito';

describe('Preferito', () => {
  let service: Preferito;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Preferito);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
