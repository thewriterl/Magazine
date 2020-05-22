import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPublisherMagazine } from 'app/shared/model/publisher-magazine.model';
import { PublisherMagazineService } from './publisher-magazine.service';
import { PublisherMagazineDeleteDialogComponent } from './publisher-magazine-delete-dialog.component';

@Component({
  selector: 'jhi-publisher-magazine',
  templateUrl: './publisher-magazine.component.html',
})
export class PublisherMagazineComponent implements OnInit, OnDestroy {
  publishers?: IPublisherMagazine[];
  eventSubscriber?: Subscription;
  currentSearch: string;

  constructor(
    protected publisherService: PublisherMagazineService,
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
      this.publisherService
        .search({
          query: this.currentSearch,
        })
        .subscribe((res: HttpResponse<IPublisherMagazine[]>) => (this.publishers = res.body || []));
      return;
    }

    this.publisherService.query().subscribe((res: HttpResponse<IPublisherMagazine[]>) => (this.publishers = res.body || []));
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInPublishers();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IPublisherMagazine): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInPublishers(): void {
    this.eventSubscriber = this.eventManager.subscribe('publisherListModification', () => this.loadAll());
  }

  delete(publisher: IPublisherMagazine): void {
    const modalRef = this.modalService.open(PublisherMagazineDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.publisher = publisher;
  }
}
