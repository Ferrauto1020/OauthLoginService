import { InjectionToken } from '@angular/core';

export const LOCAL_STORAGE = new InjectionToken<Storage>('Local Storage', {
  providedIn: 'root',
  factory: () => typeof window !== 'undefined' ? window.localStorage : new NoopStorage()
});

class NoopStorage implements Storage {
  length: number = 0;
  clear(): void { }
  getItem(key: string): string | null { return null; }
  key(index: number): string | null { return null; }
  removeItem(key: string): void { }
  setItem(key: string, value: string): void { }
}
