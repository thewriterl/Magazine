import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IMagazineMagazine } from 'app/shared/model/magazine-magazine.model';
import { MagazineMagazineService } from './magazine-magazine.service';
import { MagazineMagazineDeleteDialogComponent } from './magazine-magazine-delete-dialog.component';

@Component({
  selector: 'jhi-magazine-magazine',
  templateUrl: './magazine-magazine.component.html',
})
export class MagazineMagazineComponent implements OnInit, OnDestroy {
  magazines?: IMagazineMagazine[];
  eventSubscriber?: Subscription;
  currentSearch: string;

  constructor(
    protected magazineService: MagazineMagazineService,
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
      this.magazineService
        .search({
          query: this.currentSearch,
        })
        .subscribe((res: HttpResponse<IMagazineMagazine[]>) => (this.magazines = res.body || []));
      return;
    }

    this.magazineService.query().subscribe((res: HttpResponse<IMagazineMagazine[]>) => (this.magazines = res.body || []));
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInMagazines();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IMagazineMagazine): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInMagazines(): void {
    this.eventSubscriber = this.eventManager.subscribe('magazineListModification', () => this.loadAll());
  }

  delete(magazine: IMagazineMagazine): void {
    const modalRef = this.modalService.open(MagazineMagazineDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.magazine = magazine;
  }
}
