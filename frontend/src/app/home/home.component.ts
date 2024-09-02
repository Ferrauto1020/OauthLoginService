import { Component, OnInit } from '@angular/core';

import { MyHttpClientService } from '../shared/services/MyHttpClientService';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {
  url: any;
  

  constructor(private http: MyHttpClientService, private route: Router) {
    this.url = {'google':'',
                'facebook':'',
                'azure':'',
                
    };
    
  }
  ngOnInit(): void {
    this.getUrl();
  }

  logout() {
    this.http.clearToken();
  }

  
  navigateToPrivate() {
    this.route.navigate(['/private'], { replaceUrl: true });
  }

  getUrl() {
    this.http.get('/google/url').subscribe((data: any) => {
      this.url['google'] = data;
    });
    this.http.get('/azure/url').subscribe((data: any) => {
      console.log(data);
      this.url['azure'] = data;
    });
          this.http.get('/facebook/url').subscribe((data: any) => {
      this.url['facebook'] = data;
    });
    
  }

}
