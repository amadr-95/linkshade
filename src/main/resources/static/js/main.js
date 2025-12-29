document.addEventListener('DOMContentLoaded', function () {
    document.getElementById("year").innerHTML = new Date().getFullYear().toString();
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

    // Set all returnUrl inputs
    const returnUrlInputs = document.querySelectorAll('.return-url-input');
    returnUrlInputs.forEach(input => {
        input.value = window.location.href;
    });

    // Set all userTimezone inputs
    const userTimeZoneInputs = document.querySelectorAll('.user-timezone-input');
    userTimeZoneInputs.forEach(input => {
        try {
            input.value = Intl.DateTimeFormat().resolvedOptions().timeZone;
        } catch(e) {
            console.warn('Could not detect timezone, will use server default');
        }
    });
});