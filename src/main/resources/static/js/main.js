document.addEventListener('DOMContentLoaded', function () {
    const clipboardButtons = document.querySelectorAll('.clipboard-button');

    clipboardButtons.forEach(button => {
        button.addEventListener('click', function () {
            const url = this.getAttribute('data-url');
            navigator.clipboard.writeText(url)
                .then(() => {
                    // change button appearance
                    this.classList.replace('btn-outline-secondary', 'btn-success');
                    this.querySelector('i').classList.replace('bi-clipboard', 'bi-check-lg');

                    // restore button appearance after 0.5s
                    setTimeout(() => {
                        this.classList.replace('btn-success', 'btn-outline-secondary');
                        this.querySelector('i').classList.replace('bi-check-lg', 'bi-clipboard');
                    }, 1000);
                })
                .catch(err => {
                    console.error('Copy error: ', err);
                });
        });
    });

    // Only in my-urls html page when the user is logged in
    const lengthRange = document.getElementById('urlLength');
    const output = document.getElementById('urlLengthValue');
    const switchElement = document.getElementById('customModeSwitch');
    const rangeFormField = document.querySelector('.range-container');
    const inputFormField = document.querySelector('.input-container');

    if (lengthRange && output && switchElement) { //check they're present (when the user is logged in)
        let urlLength = lengthRange;
        output.innerText = urlLength.value;

        lengthRange.addEventListener("input", (event) => {
            urlLength = event.target.value;
            output.innerText = urlLength;
        })

        function checkInitialState() {
            // check initial state when the page is loaded
            if (switchElement.checked) {
                rangeFormField.classList.add('d-none');
                inputFormField.classList.remove('d-none');
                inputFormField.style.opacity = '1';
            } else {
                inputFormField.classList.add('d-none');
                rangeFormField.classList.remove('d-none');
                rangeFormField.style.opacity = '1';
            }
        }

        checkInitialState();

        function transitionElements(elementToHide, elementToShow) {
            // Hide first element with transition
            elementToHide.style.opacity = '0';
            elementToHide.style.transition = 'opacity 0.2s';

            setTimeout(() => {
                // change elements visibility
                elementToHide.classList.add('d-none');
                elementToShow.classList.remove('d-none');
                elementToShow.style.opacity = '0';

                // Show second element with transition
                setTimeout(() => {
                    elementToShow.style.opacity = '1';
                    elementToShow.style.transition = 'opacity 0.2s';
                }, 10);
            }, 200);
        }

        switchElement.addEventListener('change', function () {
            const showElement = this.checked ? inputFormField : rangeFormField;
            const hideElement = this.checked ? rangeFormField : inputFormField;
            transitionElements(hideElement, showElement);
        })
    }

    // Delete selected urls when the user is logged in
    const selectAllCheckboxButton = document.getElementById('selectAll');
    const urlCheckboxes = document.querySelectorAll('.url-checkbox');
    const deleteButton = document.getElementById('deleteSelectedBtn');

    if (selectAllCheckboxButton && urlCheckboxes && deleteButton) {

        selectAllCheckboxButton.addEventListener('change', (event) => {
            const isChecked = event.target.checked;
            urlCheckboxes.forEach(checkbox => {
                checkbox.checked = isChecked;
            });
            updateDeleteButtonState();
        })

        urlCheckboxes.forEach(checkbox => {
            checkbox.addEventListener('change', () => {
                updateDeleteButtonState();
                //if all are checked, also check the select all one
                selectAllCheckboxButton.checked = Array.from(urlCheckboxes).every(checkbox => checkbox.checked);
            })
        })

        function updateDeleteButtonState() {
            const anyChecked = Array.from(urlCheckboxes).some(checkbox => checkbox.checked);
            deleteButton.disabled = !anyChecked;
        }
    }
});