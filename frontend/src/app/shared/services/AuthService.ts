import { HttpClient, HttpParams } from '@angular/common/http';
import {  Injectable } from '@angular/core';
import { MyHttpClientService } from './MyHttpClientService';
@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private googleTokenInfoUrl = 'https://oauth2.googleapis.com/tokeninfo';
 private backendVerifyFacebookTokenUrl = 'http://localhost:8080/facebook/verifyToken'
 private backendVerifyMicrosoftTokenUrl = 'http://localhost:8080/azure/verifyToken'

  constructor(private http: HttpClient,private service: MyHttpClientService,
  ) {}

verifyToken(){
    const provider = this.service.getProvider()
    let urlProvider = 
        provider==='google'?this.googleTokenInfoUrl:
        provider==='azure'?this.backendVerifyMicrosoftTokenUrl:
        this.backendVerifyFacebookTokenUrl
    return this.http.get(`${urlProvider}`, {'params':{'access_token':this.getAuthToken()}})
}

  getAuthToken(): any {
    return this.service.getAccessToken();
  }
  getProvider(){
    return this.service.getProvider();
  }
}