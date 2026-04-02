import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VerificaEmail } from './verifica-email';

describe('VerificaEmail', () => {
  let component: VerificaEmail;
  let fixture: ComponentFixture<VerificaEmail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerificaEmail],
    }).compileComponents();

    fixture = TestBed.createComponent(VerificaEmail);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
