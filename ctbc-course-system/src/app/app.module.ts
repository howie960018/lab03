import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { AuthInterceptor } from './auth/auth.interceptor';

import { NavbarComponent } from './navbar/navbar.component';
import { HomeComponent } from './home/home.component';
import { PublicCoursesComponent } from './public-courses/public-courses.component';
import { CourseDetailComponent } from './course-detail/course-detail.component';
import { CartComponent } from './cart/cart.component';
import { OrderListComponent } from './orders/order-list.component';
import { OrderDetailComponent } from './orders/order-detail.component';
import { MyCoursesComponent } from './my-courses/my-courses.component';
import { ProfileComponent } from './profile/profile.component';

import { CategoryComponent } from './category/category.component';
import { CourseComponent } from './course/course.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';

import { AdminDashboardComponent } from './admin/admin-dashboard.component';
import { AdminChaptersComponent } from './admin/admin-chapters.component';
import { AdminReviewsComponent } from './admin/admin-reviews.component';

import { InstructorDashboardComponent } from './instructor/instructor-dashboard.component';
import { InstructorCoursesComponent } from './instructor/instructor-courses.component';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    HomeComponent,
    PublicCoursesComponent,
    CourseDetailComponent,
    CartComponent,
    OrderListComponent,
    OrderDetailComponent,
    MyCoursesComponent,
    ProfileComponent,
    CategoryComponent,
    CourseComponent,
    LoginComponent,
    RegisterComponent,
    AdminDashboardComponent,
    AdminChaptersComponent,
    AdminReviewsComponent,
    InstructorDashboardComponent,
    InstructorCoursesComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
