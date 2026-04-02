import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Statistiche } from './statistiche';

describe('Statistiche', () => {
  let component: Statistiche;
  let fixture: ComponentFixture<Statistiche>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Statistiche],
    }).compileComponents();

    fixture = TestBed.createComponent(Statistiche);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
