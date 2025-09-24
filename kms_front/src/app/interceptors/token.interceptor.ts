import { HttpInterceptorFn } from '@angular/common/http';

export const tokenInterceptor: HttpInterceptorFn = (req, next) => {
  // Check if running in a browser environment
  if (typeof localStorage === 'undefined') {
    return next(req);
  }

  const token = localStorage.getItem('authToken');

  if (token) {
    // Clone the request to add the new header.
    const cloned = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`),
    });
    return next(cloned);
  }

  return next(req);
};
