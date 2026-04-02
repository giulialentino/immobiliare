import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BadgeService {
  private countSubject = new BehaviorSubject<number>(0);
  count$ = this.countSubject.asObservable();

  setCount(n: number) {
    this.countSubject.next(n);
  }

  decrement(n: number = 1) {
    const current = this.countSubject.getValue();
    this.countSubject.next(Math.max(0, current - n));
  }

  reset() {
    this.countSubject.next(0);
  }
}