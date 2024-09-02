import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Inject, Injectable } from '@angular/core';
import { Observable, catchError, map, tap, throwError } from 'rxjs';

import { LOCAL_STORAGE } from './local-storage/local-storage.token';
import { response } from 'express';
import { Console } from 'console';

@Injectable({
  providedIn: 'root',
})
export class MyHttpClientService {
  constructor(
    private http: HttpClient,
    @Inject(LOCAL_STORAGE) private localStorage: Storage
  ) {
    
    this.urlServer = 'http://localhost:8080';
    this.token = '';
    this.provider = '';
  }

  
  getAccessToken(): string | null {
    return this.localStorage.getItem('access_token');
  }

  setAccessToken(token: string | null): void {
    if (token !== null) {
      this.localStorage.setItem('access_token', token);
    } else {
      this.localStorage.removeItem('access_token');
    }
  }
  clearToken() {
    this.localStorage.removeItem('access_token');
    
    
  }
  token: string = '';
  urlServer: string;
  provider: string;

  get(url: string): any {
    return this.http.get<string>(`${this.urlServer}${url}`,{responseType: 'text' as 'json'})
    .pipe(
      map((response:any)=>
    response as string
    ))
  }

  //rendiamo la funzione generica cosi da non doverla creare più volte
  getToken(providerPassed:string, codeValue: string): any {
    return this.http
      .get(`${this.urlServer}/${providerPassed}/getToken`, {
        params: { codeValue },
        observe: 'response',
      })
      .pipe(
        map((response: any) => {
          if (response.status == 200 && response.body !== null) {
            this.token = response.body.access_token;
            this.setAccessToken(this.token);
            this.provider=providerPassed;
            return true;
          } else {
            return false;
          }
        })
      );
  }

  

  getProvider():any
  {
    return this.provider;
  }

  //user info
  getUserInfo(): Observable<any> {
    const providerToUser = this.provider=="microsoft"?"azure":this.provider
    const token = this.getAccessToken();
    if (!token) {
      throw new Error('No access token found');
    }

    return this.http.get(`${this.urlServer}/${providerToUser}/userInfo`, { params: { accessToken: token } });
  
  }


}

