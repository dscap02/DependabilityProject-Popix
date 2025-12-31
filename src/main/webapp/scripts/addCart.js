document.addEventListener('DOMContentLoaded', function () {
    const addToCartButton = document.getElementById('addToCartButton');

    if (addToCartButton) {
        addToCartButton.addEventListener('click', function () {
            const productId = this.getAttribute('data-product-id');
            const quantityInput = document.getElementById('quantity');
            const quantity = quantityInput ? Number.parseInt(quantityInput.value) : 1;

            if (quantity <= 0) {
                Swal.fire('Errore', 'La quantità deve essere almeno 1.', 'error');
                return;
            }

            fetch(contextPath + '/addToCart', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `productId=${productId}&quantity=${quantity}`
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Errore di rete');
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.success) {
                        Swal.fire({
                            icon: 'success',
                            title: 'Successo!',
                            text: data.message,
                            showConfirmButton: false,
                            timer: 1500
                        });
                    } else {
                        Swal.fire({
                            icon: 'error',
                            title: 'Errore!',
                            text: data.message,
                            showConfirmButton: true
                        });
                    }
                })
                .catch(error => {
                    console.error('Errore:', error);
                    Swal.fire('Errore', 'Si è verificato un problema. Riprova.', 'error');
                });
        });
    }
});
