import { Component, Input, OnInit, EventEmitter, Output, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-dropdown',
  templateUrl: './dropdown.component.html',
  styleUrls: ['./dropdown.component.css']
})
export class DropdownComponent implements OnInit, OnChanges {

  @Input() items = [];
  @Input() isDisabled: boolean = false;
  @Input() requiredInfo: boolean = false;
  @Input() placeholder: string;
  @Input() selectedItem: string;
  @Output() selected = new EventEmitter();
  @Output() closeEventEmitter = new EventEmitter();

  itemList = [];
  selectedOption: any;
  constructor() { }

  onClose() {
    this.closeEventEmitter.emit();
  }

  massageData(list: any, selectedOption: any) {
    if (list) {
      this.itemList = [];
      if (list.length > 0 && typeof list[0] == 'object') {
        for (let i = 0; i < list.length; i++) {
          this.itemList.push(list[i].text);
        }
      }
      else this.itemList = list;

      if (typeof selectedOption == 'object' && selectedOption[0]) {
        this.selectedOption = selectedOption[0].text;
      }
      else {
        this.selectedOption = selectedOption;
      }
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.massageData(this.items, this.selectedItem);
  }

  ngOnInit(): void {
  }

  selectedValue(event: any) {
    this.selected.emit(event);
  }
}

