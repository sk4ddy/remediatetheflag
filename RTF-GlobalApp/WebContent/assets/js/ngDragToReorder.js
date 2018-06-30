(function () {
  'use strict';

  angular.module('ngDragToReorder', [])
    .factory('ngDragToReorder', function () {
      return {
        isSupported: function () {
          var div = document.createElement('div');
          return ('draggable' in div) || ('ondragstart' in div && 'ondrop' in div);
        }
      };
    })
    .directive('dragToReorder', function () {
      return {
        restrict: 'A',
        scope: true,
        controller: ['$parse', '$attrs', '$scope', '$element', function ($parse, $attrs, $scope, $element) {
          this.getList = function () {
            //return a shallow copy and prevents the updating of the parent object
            return $parse($attrs.dragToReorder)($scope).slice();
          };
          this.elem = function () {
            return $element[0];
          };
        }]
      }
    })
    .directive('dragToReorderBind', function () {
      return {
        restrict: 'A',
        scope: true,
        controller: ['$parse', '$attrs', '$scope', '$element', function ($parse, $attrs, $scope, $element) {
          this.getList = function () {
            return $parse($attrs.dragToReorderBind)($scope);
          };
          this.elem = function () {
            return $element[0];
          };
        }]
      }
    })
    .directive('dtrDraggable', ['ngDragToReorder', '$parse', function (ngDragToReorder, $parse) {
      return {
        restrict: 'A',
        require: ['?^^dragToReorder', '?^^dragToReorderBind'],
        link: function (scope, element, attrs, ctrls) {

          if (!ngDragToReorder.isSupported()) return;

          var el = element[0], list, stringIdx, int, item, listCtrl = ctrls[0] ? ctrls[0] : ctrls[1],
            newIdx, prevIdx, target, offsetY, dragging = 'dtr-dragging', over = 'dtr-over',
            droppingAbove = 'dtr-dropping-above', droppingBelow = 'dtr-dropping-below', transition = 'dtr-transition',
            eventName = 'dropped', delay = 1000, loaded = false, above = [], below = [], i, j,
            topOffset = 50, bottomOffset = 50, windowHeight, listHeight, slowScroll = false, fastScroll = false,
            listScrollbar = false, listEl, listScroll = false, itemOffestTop, startY, listTopY,
            isChrome = !!window.chrome && !!window.chrome.webstore, isIE = !!document.documentMode,
            isEdge = !isIE && !!window.StyleMedia, isFirefox = typeof InstallTrigger !== 'undefined',
            isSafari = /constructor/i.test(window.HTMLElement) || (function (p) {
                return p.toString() === "[object SafariRemoteNotification]";
              })(!window['safari'] || safari.pushNotification);

          if (attrs.dtrEvent) {
            eventName = attrs.dtrEvent || 'dropped';
          }

          if (attrs.dtrInit) {
            attrs.$observe('dtrInit', function (val) {
              if (val && val !== 'false') {
                addListeners();
              } else if (loaded) {
                removeListeners();
              }
              loaded = true;
            });
          } else {
            addListeners();
          }

          function addListeners() {
            el.draggable = true;
            el.addEventListener('dragstart', dragStart, false);
            el.addEventListener('dragend', dragEnd, false);
            el.addEventListener('dragenter', dragEnter, false);
            el.addEventListener('dragleave', dragLeave, false);
            el.addEventListener('dragover', dragOver, false);
            el.addEventListener('drop', drop, false);
            if (isChrome || isIE || isEdge)
              el.addEventListener('drag', drag, false);
          }

          function removeListeners() {
            el.draggable = false;
            el.removeEventListener('dragstart', dragStart, false);
            el.removeEventListener('dragend', dragEnd, false);
            el.removeEventListener('dragenter', dragEnter, false);
            el.removeEventListener('dragleave', dragLeave, false);
            el.removeEventListener('dragover', dragOver, false);
            el.removeEventListener('drop', drop, false);
            el.removeEventListener('drag', drag, false);
          }

          function drag(e) {
            if (listScrollbar) {
              if (e.pageY - listTopY <= 25) {
                if (listEl.scrollTop > 0) {
                  if (!listScroll) {
                    listScroll = true;
                    scrollList(-3);
                  }
                }
              } else if (listTopY + listHeight - e.pageY <= 25) {
                if (listEl.scrollTop < listEl.scrollHeight - listHeight) {
                  if (!listScroll) {
                    listScroll = true;
                    scrollList(3);
                  }
                }
              } else listScroll = false;
            }

            if (e.clientY <= topOffset / 2) {
              if (!fastScroll) {
                slowScroll = false;
                fastScroll = true;
                scrollFast(-6);
              }
            } else if (e.clientY <= topOffset) {
              if (!slowScroll) {
                fastScroll = false;
                slowScroll = true;
                scrollSlow(-3);
              }
            } else if (e.clientY >= windowHeight - bottomOffset / 2) {
              if (!fastScroll) {
                slowScroll = false;
                fastScroll = true;
                scrollFast(6);
              }
            } else if (e.clientY >= windowHeight - bottomOffset) {
              if (!slowScroll) {
                fastScroll = false;
                slowScroll = true;
                scrollSlow(3);
              }
            } else {
              slowScroll = false;
              fastScroll = false;
            }
          }

          function scrollSlow(step) {
            window.scrollBy(0, step);
            if (slowScroll)
              setTimeout(function () {
                scrollSlow(step);
              }, 20)
          }

          function scrollFast(step) {
            window.scrollBy(0, step);
            if (fastScroll)
              setTimeout(function () {
                scrollFast(step);
              }, 20)
          }

          function scrollList(step) {
            if (listScroll) {
              listEl.scrollTop = listEl.scrollTop + step;
              setTimeout(function () {
                scrollList(step);
              }, 20)
            }
          }


          function dragStart(e) {
            windowHeight = window.innerHeight;
            listHeight = this.parentElement.clientHeight;
            listScrollbar = this.parentElement.scrollHeight > listHeight;
            listTopY = angular.element(this.parentElement).offset().top;
            itemOffestTop = angular.element(e.target).position().top;
            startY = e.pageY;
            listEl = this.parentElement;
            e.dataTransfer.effectAllowed = 'move';
            stringIdx = scope.$index.toString();
            e.dataTransfer.setData('text', stringIdx);
            this.classList.add(dragging);
            this.classList.add(transition);
            return false;
          }

          function dragEnd(e) {
            slowScroll = false;
            fastScroll = false;
            listScroll = false;
            target = this;
            target.classList.remove(dragging);
            if (attrs.dtrTransitionTimeout) {
              int = parseInt($parse(attrs.dtrTransitionTimeout)(scope), 10);
              if (typeof int === 'number' && int >= 0)
                delay = int;
            }
            setTimeout(function () {
              target.classList.remove(transition)
            }, delay);
            cleanupClasses();
            return false;
          }


          function drop(e) {
            e.preventDefault();
            if (e.stopPropagation) e.stopPropagation();
            prevIdx = parseInt(e.dataTransfer.getData('text'), 10);
            this.classList.remove(over);
            if (this.classList.contains(droppingAbove)) {
              if (prevIdx < scope.$index) {
                newIdx = scope.$index - 1;
              } else {
                newIdx = scope.$index;
              }
            } else {
              if (prevIdx < scope.$index) {
                newIdx = scope.$index;
              } else {
                newIdx = scope.$index + 1;
              }
            }

            list = listCtrl.getList();
            item = list.splice(prevIdx, 1)[0];
            list.splice(newIdx, 0, item);

            scope.$apply(function () {
              scope.$emit('dragToReorder.' + eventName, {
                list: list,
                item: item,
                newIndex: newIdx,
                prevIndex: prevIdx
              });
            });

            this.classList.remove(over);
            this.classList.remove(droppingAbove);
            this.classList.remove(droppingBelow);
            /* if (this.previousElementSibling) {
             this.previousElementSibling.classList.remove(over);
             this.previousElementSibling.classList.remove(droppingAbove);
             this.previousElementSibling.classList.remove(droppingBelow);
             }
             if (this.nextElementSibling) {
             this.nextElementSibling.classList.remove(over);
             this.nextElementSibling.classList.remove(droppingAbove);
             this.nextElementSibling.classList.remove(droppingBelow);
             }*/
          }


          function dragOver(e) {
            e.preventDefault();
            e.dataTransfer.dropEffect = 'move';
            offsetY = e.offsetY;
            if (!this.classList.contains(dragging)) {
              if (offsetY < (this.offsetHeight / 2)) {
                this.classList.remove(droppingBelow);
                this.classList.add(droppingAbove);
                /* if (this.previousElementSibling)
                 this.previousElementSibling.classList.add(droppingBelow);
                 if (this.nextElementSibling)
                 this.nextElementSibling.classList.remove(droppingAbove);*/
              } else {
                this.classList.remove(droppingAbove);
                this.classList.add(droppingBelow);
                /*  if (this.previousElementSibling)
                 this.previousElementSibling.classList.remove(droppingBelow);
                 if (this.nextElementSibling)
                 this.nextElementSibling.classList.add(droppingAbove);*/
              }
            }
            return false;
          }

          function dragEnter(e) {
            e.preventDefault();
            if (!this.classList.contains(dragging))
              this.classList.add(over);
          }

          function dragLeave(e) {
            this.classList.remove(over);
            this.classList.remove(droppingAbove);
            this.classList.remove(droppingBelow);
            return false;
          }

          function cleanupClasses() {
            above = document.querySelectorAll('.' + droppingAbove);
            below = document.querySelectorAll('.' + droppingBelow);
            if (above.length) {
              for (i = 0; i < above.length; i++) {
                above[i].classList.remove(droppingAbove);
              }
            }
            if (below.length) {
              for (j = 0; j < below.length; j++) {
                below[j].classList.remove(droppingBelow);
              }
            }
          }

        }
      };

    }]);

})();
