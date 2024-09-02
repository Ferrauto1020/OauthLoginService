import { Component, OnInit } from '@angular/core';

import { MyHttpClientService } from '../shared/services/MyHttpClientService';
import { Router } from '@angular/router';

@Component({
  selector: 'app-private',
  templateUrl: './private.component.html',
  styleUrl: './private.component.scss'
})


export class PrivateComponent implements OnInit {
  userInfo: any;
  constructor(private http: MyHttpClientService, private route: Router) {}
  ngOnInit(): void {
    this.http.getUserInfo().subscribe((data) => {
      this.userInfo = data;
      console.log('User Info:', this.userInfo);
    });
  }

  logout() {
    
    this.http.clearToken();
    this.route.navigate(['/'], { replaceUrl: true })

  }
  
}
