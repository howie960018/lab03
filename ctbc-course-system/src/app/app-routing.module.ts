import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { CourseCatalogComponent } from './course-catalog/course-catalog.component';
import { CategoryCatalogComponent } from './category-catalog/category-catalog.component';
import { CategoryComponent } from './category/category.component';
import { CourseComponent } from './course/course.component';

import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { AuthGuard } from './auth/auth.guard';

const routes: Routes = [

  // ===== 前台（公開）=====
  {
    path: '',
    component: CategoryCatalogComponent
  },

  {
    path: 'categories',
    component: CategoryCatalogComponent
  },

  {
    path: 'categories/:id',
    component: CourseCatalogComponent
  },

  {
    path: 'courses',
    component: CourseCatalogComponent
  },

  // ===== 認證 =====
  {
    path: 'login',
    component: LoginComponent
  },

  {
    path: 'register',
    component: RegisterComponent
  },

  // ===== 後台（需登入）=====
  {
    path: 'admin',
    canActivate: [AuthGuard], // ✅ 整包 admin 都受保護
    children: [
      {
        path: 'categories',
        component: CategoryComponent
      },
      {
        path: 'courses',
        component: CourseComponent
      }
    ]
  },

  // ===== fallback =====
  {
    path: '**',
    redirectTo: ''
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }