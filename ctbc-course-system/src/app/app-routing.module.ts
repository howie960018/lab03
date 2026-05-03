import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { CategoryComponent } from './category/category.component';
import { CourseComponent } from './course/course.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';

const routes: Routes = [
  { path: '', redirectTo: 'categories', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'categories', component: CategoryComponent },
  { path: 'courses', component: CourseComponent },
  { path: '**', redirectTo: 'categories' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
