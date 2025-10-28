document.addEventListener('DOMContentLoaded', function () {

    const sortableProperties = document.querySelectorAll('.sortable-property');

    // initial sorting state
    const currentSort = new URLSearchParams(window.location.search).get('sort') || 'createdAt,DESC';
    const [property, direction] = currentSort.split(',');

    updateSortIcons(property, direction);

    sortableProperties.forEach(property => {
        property.addEventListener('click', function (e) {
            e.preventDefault();

            const propName = this.closest('th').dataset.column;
            const currentDirection = this.closest('th').dataset.direction || 'DESC';
            const newDirection = currentDirection === 'ASC' ? 'DESC' : 'ASC';
            this.closest('th').dataset.direction = newDirection;

            // create URL (TODO: clean the URL by removing parts not expected like /create-url
            //  when sending wrong values for creating URLs)
            const url = new URL(window.location.href);
            const params = new URLSearchParams(url.search);
            params.set('sort', `${propName},${newDirection}`);
            url.search = params.toString();

            // scroll should be restored
            sessionStorage.setItem('shouldRestoreScroll', 'true');

            // Go to the new URL
            window.location.href = url.toString();
        });
    });

    function updateSortIcons(activeProp, direction) {
        sortableProperties.forEach(property => {
            const th = property.closest('th');
            const propName = th.dataset.column;
            const icon = property.querySelector('.sort-icon');

            //reset all icons
            icon.classList.remove('bi-caret-down-fill', 'bi-caret-up-fill');

            if (propName === activeProp) {
                icon.classList.add(direction === 'ASC' ? 'bi-caret-up-fill' : 'bi-caret-down-fill');
                th.dataset.direction = direction;
            } else {
                icon.classList.add('bi-caret-down-fill');
                th.dataset.direction = 'DESC';
            }
        });
    }
});