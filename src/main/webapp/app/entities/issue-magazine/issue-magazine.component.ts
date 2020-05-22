import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiDataUtils } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IIssueMagazine } from 'app/shared/model/issue-magazine.model';
import { IssueMagazineService } from './issue-magazine.service';
import { IssueMagazineDeleteDialogComponent } from './issue-magazine-delete-dialog.component';

@Component({
  selector: 'jhi-issue-magazine',
  templateUrl: './issue-magazine.component.html',
})
export class IssueMagazineComponent implements OnInit, OnDestroy {
  issues?: IIssueMagazine[];
  eventSubscriber?: Subscription;
  currentSearch: string;

  constructor(
    protected issueService: IssueMagazineService,
    protected dataUtils: JhiDataUtils,
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
      this.issueService
        .search({
          query: this.currentSearch,
        })
        .subscribe((res: HttpResponse<IIssueMagazine[]>) => (this.issues = res.body || []));
      return;
    }

    this.issueService.query().subscribe((res: HttpResponse<IIssueMagazine[]>) => (this.issues = res.body || []));
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInIssues();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IIssueMagazine): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(contentType = '', base64String: string): void {
    return this.dataUtils.openFile(contentType, base64String);
  }

  registerChangeInIssues(): void {
    this.eventSubscriber = this.eventManager.subscribe('issueListModification', () => this.loadAll());
  }

  delete(issue: IIssueMagazine): void {
    const modalRef = this.modalService.open(IssueMagazineDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.issue = issue;
  }
}
