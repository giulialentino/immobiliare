import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModificaAnnuncio } from './modifica-annuncio';

describe('ModificaAnnuncio', () => {
  let component: ModificaAnnuncio;
  let fixture: ComponentFixture<ModificaAnnuncio>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModificaAnnuncio],
    }).compileComponents();

    fixture = TestBed.createComponent(ModificaAnnuncio);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
