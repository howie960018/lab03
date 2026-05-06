import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { CategoryComponent } from './category/category.component';
import { CourseComponent } from './course/course.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { AuthInterceptor } from './auth/auth.interceptor';
import { CourseCatalogComponent } from './course-catalog/course-catalog.component';
import { CategoryCatalogComponent } from './category-catalog/category-catalog.component';

@NgModule({
  declarations: [
    AppComponent,
    CategoryComponent,
    CourseComponent,
    LoginComponent,
    RegisterComponent,
    CategoryCatalogComponent,
    CourseCatalogComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule
  ],
  // 全站共用的服務櫃檯
  providers: [
    // 網站有人要送 HTTP 請求時，先交給 AuthInterceptor 看要不要加 JWT
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }