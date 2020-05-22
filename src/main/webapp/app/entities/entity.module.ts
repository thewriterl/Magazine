import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'magazine-magazine',
        loadChildren: () => import('./magazine-magazine/magazine-magazine.module').then(m => m.PixelmagsMagazineMagazineModule),
      },
      {
        path: 'subscription-plan-magazine',
        loadChildren: () =>
          import('./subscription-plan-magazine/subscription-plan-magazine.module').then(m => m.PixelmagsSubscriptionPlanMagazineModule),
      },
      {
        path: 'log-magazine',
        loadChildren: () => import('./log-magazine/log-magazine.module').then(m => m.PixelmagsLogMagazineModule),
      },
      {
        path: 'purchase-magazine',
        loadChildren: () => import('./purchase-magazine/purchase-magazine.module').then(m => m.PixelmagsPurchaseMagazineModule),
      },
      {
        path: 'customer-magazine',
        loadChildren: () => import('./customer-magazine/customer-magazine.module').then(m => m.PixelmagsCustomerMagazineModule),
      },
      {
        path: 'device-magazine',
        loadChildren: () => import('./device-magazine/device-magazine.module').then(m => m.PixelmagsDeviceMagazineModule),
      },
      {
        path: 'publisher-magazine',
        loadChildren: () => import('./publisher-magazine/publisher-magazine.module').then(m => m.PixelmagsPublisherMagazineModule),
      },
      {
        path: 'issue-magazine',
        loadChildren: () => import('./issue-magazine/issue-magazine.module').then(m => m.PixelmagsIssueMagazineModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class PixelmagsEntityModule {}
