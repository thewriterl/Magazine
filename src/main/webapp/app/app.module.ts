import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { PixelmagsSharedModule } from 'app/shared/shared.module';
import { PixelmagsCoreModule } from 'app/core/core.module';
import { PixelmagsAppRoutingModule } from './app-routing.module';
import { PixelmagsHomeModule } from './home/home.module';
import { PixelmagsEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ErrorComponent } from './layouts/error/error.component';

@NgModule({
  imports: [
    BrowserModule,
    PixelmagsSharedModule,
    PixelmagsCoreModule,
    PixelmagsHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    PixelmagsEntityModule,
    PixelmagsAppRoutingModule,
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent],
  bootstrap: [MainComponent],
})
export class PixelmagsAppModule {}
