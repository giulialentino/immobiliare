import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreaAnnuncio } from './crea-annuncio';

describe('CreaAnnuncio', () => {
  let component: CreaAnnuncio;
  let fixture: ComponentFixture<CreaAnnuncio>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreaAnnuncio],
    }).compileComponents();

    fixture = TestBed.createComponent(CreaAnnuncio);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
