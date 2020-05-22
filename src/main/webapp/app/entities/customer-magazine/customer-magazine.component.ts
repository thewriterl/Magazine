import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICustomerMagazine } from 'app/shared/model/customer-magazine.model';
import { CustomerMagazineService } from './customer-magazine.service';
import { CustomerMagazineDeleteDialogComponent } from './customer-magazine-delete-dialog.component';

@Component({
  selector: 'jhi-customer-magazine',
  templateUrl: './customer-magazine.component.html',
})
export class CustomerMagazineComponent implements OnInit, OnDestroy {
  customers?: ICustomerMagazine[];
  eventSubscriber?: Subscription;
  currentSearch: string;

  constructor(
    protected customerService: CustomerMagazineService,
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
      this.customerService
        .search({
          query: this.currentSearch,
        })
        .subscribe((res: HttpResponse<ICustomerMagazine[]>) => (this.customers = res.body || []));
      return;
    }

    this.customerService.query().subscribe((res: HttpResponse<ICustomerMagazine[]>) => (this.customers = res.body || []));
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInCustomers();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ICustomerMagazine): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInCustomers(): void {
    this.eventSubscriber = this.eventManager.subscribe('customerListModification', () => this.loadAll());
  }

  delete(customer: ICustomerMagazine): void {
    const modalRef = this.modalService.open(CustomerMagazineDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.customer = customer;
  }
}
