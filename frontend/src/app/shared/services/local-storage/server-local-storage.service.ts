
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ServerLocalStorageService {
  getItem(key: string): string | null {
    return null;
  }

  setItem(key: string, value: string): void { }

  removeItem(key: string): void { }

  clear(): void { }
}
