import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecuperaPassword } from './recupera-password';

describe('RecuperaPassword', () => {
  let component: RecuperaPassword;
  let fixture: ComponentFixture<RecuperaPassword>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecuperaPassword],
    }).compileComponents();

    fixture = TestBed.createComponent(RecuperaPassword);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
