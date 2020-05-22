import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPurchaseMagazine } from 'app/shared/model/purchase-magazine.model';
import { PurchaseMagazineService } from './purchase-magazine.service';
import { PurchaseMagazineDeleteDialogComponent } from './purchase-magazine-delete-dialog.component';

@Component({
  selector: 'jhi-purchase-magazine',
  templateUrl: './purchase-magazine.component.html',
})
export class PurchaseMagazineComponent implements OnInit, OnDestroy {
  purchases?: IPurchaseMagazine[];
  eventSubscriber?: Subscription;
  currentSearch: string;

  constructor(
    protected purchaseService: PurchaseMagazineService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected activatedRoute: ActivatedRoute
  ) {
    this.currentSearch =
      this.activatedRoute.snapshot && this.activatedRoute.snapshot.queryParams['search']
        ? this.activatedRoute.snapshot.queryParams['search']
        : '';
  }

  loadAll(): void {
    if (this.currentSearch) {
      this.purchaseService
        .search({
          query: this.currentSearch,
        })
        .subscribe((res: HttpResponse<IPurchaseMagazine[]>) => (this.purchases = res.body || []));
      return;
    }

    this.purchaseService.query().subscribe((res: HttpResponse<IPurchaseMagazine[]>) => (this.purchases = res.body || []));
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInPurchases();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IPurchaseMagazine): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInPurchases(): void {
    this.eventSubscriber = this.eventManager.subscribe('purchaseListModification', () => this.loadAll());
  }

  delete(purchase: IPurchaseMagazine): void {
    const modalRef = this.modalService.open(PurchaseMagazineDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.purchase = purchase;
  }
}
