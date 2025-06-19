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
    const span = document.getElementById('urlLengthValue');
    if (lengthRange && span) { //check they're present (when the user is logged in)
        let urlLength = lengthRange;
        span.innerText = urlLength.value;

        lengthRange.addEventListener("input", (event) => {
            urlLength = event.target.value;
            span.innerText = urlLength;
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