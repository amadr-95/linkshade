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
                    console.error('Error al copiar: ', err);
                });
        });
    });
});