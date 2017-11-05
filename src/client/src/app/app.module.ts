import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { MdButtonModule } from '@angular/material';
import { MatInputModule} from '@angular/material';
import { HttpClientModule } from '@angular/common/http';
import { HttpClient } from '@angular/common/http';

import { AppComponent } from './app.component';
import { HomePageComponent } from './home-page.component';
import { PageNotFoundComponent } from './not-found.component';
import { LoginPageComponent } from './LoginPage/login-page.component';
import { AboutUsComponent } from './AboutUs/about-us.component';
import { SignUpComponent } from './SignUp/sign-up.component';
import { UserConsoleComponent} from './UserConsole/user-console.component';
import { ClassHubComponent } from './ClassHub/class-hub.component';


const appRoutes: Routes = [
  { path: 'home', component: HomePageComponent },
  { path: 'login-page', component: LoginPageComponent },
  { path: 'about-us', component: AboutUsComponent },
  { path: 'sign-up', component: SignUpComponent },
  { path: 'user-console', component: UserConsoleComponent },
  { path: 'class-hub', component: ClassHubComponent },
  { path: '',   redirectTo: '/home', pathMatch: 'full' },
  { path: '**', component: PageNotFoundComponent },
  
];

@NgModule({
  imports: [
    BrowserModule,
    FormsModule,
    MdButtonModule,
    MatInputModule,
    RouterModule.forRoot(
      appRoutes,
      { enableTracing: true } // <-- debugging purposes only
    ),
    HttpClientModule
  ],
  declarations: [
    AppComponent,
    HomePageComponent,
    LoginPageComponent,
    AboutUsComponent,
    SignUpComponent,
    UserConsoleComponent,
    ClassHubComponent,
    PageNotFoundComponent
  ],
  bootstrap: [ AppComponent ]
})
export class AppModule { }
