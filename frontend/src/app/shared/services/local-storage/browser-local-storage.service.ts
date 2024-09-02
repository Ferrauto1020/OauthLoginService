import { Injectable, Inject } from '@angular/core';
import { LOCAL_STORAGE } from './local-storage.token';


@Injectable({
  providedIn: 'root',
})
export class BrowserLocalStorageService {
  constructor(@Inject(LOCAL_STORAGE) private localStorage: Storage) {}

  getItem(key: string): string | null {
    return this.localStorage.getItem(key);
  }

  setItem(key: string, value: string): void {
    this.localStorage.setItem(key, value);
  }

  removeItem(key: string): void {
    this.localStorage.removeItem(key);
  }

  clear(): void {
    this.localStorage.clear();
  }
}
