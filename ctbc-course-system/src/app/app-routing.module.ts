import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { AuthGuard } from './auth/auth.guard';
import { AdminGuard } from './auth/admin.guard';
import { InstructorGuard } from './auth/instructor.guard';

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

import { AdminDashboardComponent } from './admin/admin-dashboard.component';
import { AdminChaptersComponent } from './admin/admin-chapters.component';
import { AdminReviewsComponent } from './admin/admin-reviews.component';

import { InstructorDashboardComponent } from './instructor/instructor-dashboard.component';
import { InstructorCoursesComponent } from './instructor/instructor-courses.component';

const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  { path: 'courses', component: PublicCoursesComponent },
  { path: 'courses/:id', component: CourseDetailComponent },

  { path: 'cart', component: CartComponent },
  { path: 'orders', component: OrderListComponent, canActivate: [AuthGuard] },
  { path: 'orders/:id', component: OrderDetailComponent, canActivate: [AuthGuard] },
  { path: 'my-courses', component: MyCoursesComponent, canActivate: [AuthGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },

  { path: 'admin/dashboard', component: AdminDashboardComponent, canActivate: [AdminGuard] },
  { path: 'admin/courses', component: CourseComponent, canActivate: [AdminGuard] },
  { path: 'admin/categories', component: CategoryComponent, canActivate: [AdminGuard] },
  { path: 'admin/chapters', component: AdminChaptersComponent, canActivate: [AdminGuard] },
  { path: 'admin/reviews', component: AdminReviewsComponent, canActivate: [AdminGuard] },

  { path: 'instructor/dashboard', component: InstructorDashboardComponent, canActivate: [InstructorGuard] },
  { path: 'instructor/courses', component: InstructorCoursesComponent, canActivate: [InstructorGuard] },

  { path: '**', redirectTo: 'home' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
