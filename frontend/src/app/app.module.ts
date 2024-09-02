import { NgModule, PLATFORM_ID } from '@angular/core';
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { isPlatformBrowser } from '@angular/common';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { AuthGuard } from './shared/services/AuthGuard';
import { LOCAL_STORAGE } from './shared/services/local-storage/local-storage.token';
import { BrowserLocalStorageService } from './shared/services/local-storage/browser-local-storage.service';
import { ServerLocalStorageService } from './shared/services/local-storage/server-local-storage.service';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [
    AuthGuard,
    provideClientHydration(),
    provideHttpClient(withFetch()),
    {
      provide: LOCAL_STORAGE,
      useFactory: (platformId: Object) => {
        if (isPlatformBrowser(platformId)) {
          return new BrowserLocalStorageService(window.localStorage);
        } else {
          return new ServerLocalStorageService();
        }
      },
      deps: [PLATFORM_ID]
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
