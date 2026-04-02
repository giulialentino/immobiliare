import { TestBed } from '@angular/core/testing';

import { Annuncio } from './annuncio';

describe('Annuncio', () => {
  let service: Annuncio;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Annuncio);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
