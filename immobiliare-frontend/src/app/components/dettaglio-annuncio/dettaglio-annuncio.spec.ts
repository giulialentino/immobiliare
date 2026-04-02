import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DettaglioAnnuncio } from './dettaglio-annuncio';

describe('DettaglioAnnuncio', () => {
  let component: DettaglioAnnuncio;
  let fixture: ComponentFixture<DettaglioAnnuncio>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DettaglioAnnuncio],
    }).compileComponents();

    fixture = TestBed.createComponent(DettaglioAnnuncio);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
