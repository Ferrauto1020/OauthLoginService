import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MyHttpClientService } from '../shared/services/MyHttpClientService';

@Component({
  selector: 'app-loading',
  templateUrl: './loading.component.html',
  styleUrl: './loading.component.scss'
})
export class LoadingComponent implements OnInit{
 
 
  constructor(private http: MyHttpClientService, private route:ActivatedRoute, private navigate:Router)
  {
    this.setToken=''
  }
  setToken: any;
  provider: any
  ngOnInit(): void {
  
      this.route.queryParams.subscribe((params) => {
        console.log(params)
        if (params['code'] !== undefined && params['scope']) {
          this.provider='google'

        }
        else  if (params['code'] !== undefined && params['state'])
        { 

          this.provider='azure'   
        }
        else{
          this.provider='facebook'
          }

           this.http.getToken(this.provider,params['code']).subscribe((result: any) => {   
              this.setToken= result  
              }
            );
      })
      
      console.log(this.setToken)
    this.navigate.navigate(['/private'])
    }
  }