import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { map, catchError, of } from 'rxjs';
import { AuthService } from './AuthService';
import { response } from 'express';

@Injectable()
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

 /*  canActivate(): any {
    const token = this.authService.getAuthToken();

    if (token) {
    
    
      return this.authService.verifyToken().pipe(
        map((response: any) =>
          
          
          (response && response.expires_in > 0) || response.includes('Token is valid'))
          
          
          ,
        catchError(() => {
          this.router.navigate(['/']);
          return of(false);
        })
      );
    
    }
    else {
      console.log("sei finito nell'ultimo else non so perché")
      this.router.navigate(['/']);
      return of(false);
    }
  } */
    canActivate(): any {
      const token = this.authService.getAuthToken();
      
    
      if (token) {
        
        return this.authService.verifyToken().pipe(
          map((response: any) => {
          
            let isValid = false;
            const provider = this.authService.getProvider();
  
            if (provider === 'google') {
              isValid = response && response.expires_in > 0; 
            } else if (provider === 'facebook' || provider === 'azure') {
              isValid = response.valid;
            } 
          }),
          catchError((error) => {
            
            this.router.navigate(['/']);
            return of(false);
          })
        );
      } else {
    
        this.router.navigate(['/']);
        return of(false);
      }
    }
}
