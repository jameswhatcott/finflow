// Track changes for each table
let incomeChanges = new Map();
let expenseChanges = new Map();

// Initialize everything when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    console.log('=== DOM Content Loaded ===');
    
    // Test if we can find the buttons
    const addButtons = document.querySelectorAll('.add-row-btn');
    console.log('Found add buttons:', addButtons.length);
    
    const saveButtons = document.querySelectorAll('.save-all-btn');
    console.log('Found save buttons:', saveButtons.length);
    
    const deleteButtons = document.querySelectorAll('.delete-row-btn');
    console.log('Found delete buttons:', deleteButtons.length);
    
    // Add event listeners to all editable inputs
    const inputs = document.querySelectorAll('.editable-input');
    console.log('Found editable inputs:', inputs.length);
    inputs.forEach(input => {
        input.addEventListener('change', function() {
            trackChange(this);
        });
        
        input.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                this.blur();
            }
        });
    });
    
    // Add event listeners to add row buttons
    addButtons.forEach(button => {
        console.log('Adding click listener to button:', button);
        button.addEventListener('click', function(e) {
            e.preventDefault();
            console.log('Button clicked!');
            const type = this.getAttribute('data-type');
            console.log('Add button clicked for type:', type);
            addRow(type);
        });
    });
    
    // Add event listeners to edit buttons
    const editButtons = document.querySelectorAll('.edit-row-btn');
    editButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            console.log('Edit button clicked');
            editRow(this);
        });
    });
    
    // Add event listeners to delete buttons
    deleteButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            console.log('Delete button clicked');
            deleteRow(this);
        });
    });
    
    // Add event listeners to save buttons
    saveButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const type = this.getAttribute('data-type');
            console.log('Save button clicked for type:', type);
            saveAllChanges(type);
        });
    });
    
    console.log('All event listeners added');
});

function trackChange(input) {
    console.log('trackChange called');
    const type = input.getAttribute('data-type');
    const index = input.getAttribute('data-index');
    const field = input.classList.contains('category-input') ? 'categoryName' : 'plannedAmount';
    const value = input.value;
    const original = input.getAttribute('data-original');
    
    if (value !== original) {
        const changes = type === 'income' ? incomeChanges : expenseChanges;
        if (!changes.has(index)) {
            changes.set(index, {});
        }
        changes.get(index)[field] = value;
    } else {
        const changes = type === 'income' ? incomeChanges : expenseChanges;
        if (changes.has(index)) {
            delete changes.get(index)[field];
            if (Object.keys(changes.get(index)).length === 0) {
                changes.delete(index);
            }
        }
    }
    
    updateSaveButton(type);
}

function updateSaveButton(type) {
    const changes = type === 'income' ? incomeChanges : expenseChanges;
    const saveBtn = document.querySelector(`.save-all-btn[data-type="${type}"]`);
    
    if (changes.size > 0) {
        saveBtn.disabled = false;
        saveBtn.textContent = `Save All Changes (${changes.size})`;
    } else {
        saveBtn.disabled = true;
        saveBtn.textContent = 'Save All Changes';
    }
}

function addRow(type) {
    console.log('addRow called with type:', type);
    
    const table = document.getElementById(`${type}-table`);
    console.log('Found table:', table);
    
    if (!table) {
        console.error('Table not found for type:', type);
        return;
    }
    
    const tbody = table.querySelector('tbody');
    console.log('Found tbody:', tbody);
    
    if (!tbody) {
        console.error('Tbody not found');
        return;
    }
    
    const totalRow = tbody.querySelector('.table-total');
    console.log('Found total row:', totalRow);
    
    if (!totalRow) {
        console.error('Total row not found');
        return;
    }
    
    const newRow = document.createElement('tr');
    newRow.className = 'data-row';
    newRow.innerHTML = `
        <td class="readonly-cell">
            <span class="category-display">New Category</span>
            <input type="text" 
                   class="editable-input category-input" 
                   value="New Category" 
                   data-original="New Category"
                   data-type="${type}"
                   data-index="new"
                   style="display: none;">
        </td>
        <td class="readonly-cell">
            <span class="amount-display">$0.00</span>
            <input type="number" 
                   class="editable-input amount-input" 
                   value="0.00" 
                   data-original="0.00"
                   data-type="${type}"
                   data-index="new"
                   step="0.01" 
                   min="0" 
                   placeholder="0.00"
                   style="display: none;">
        </td>
        <td class="readonly-cell amount-cell amount-${type === 'income' ? 'positive' : 'negative'}">$0.00</td>
        <td class="readonly-cell amount-cell amount-neutral">$0.00</td>
        <td class="actions-cell">
            <button class="edit-row-btn" data-type="${type}" title="Edit">
                <i class="fas fa-pencil-alt"></i>
            </button>
            <button class="delete-row-btn" data-type="${type}" title="Delete">
                <i class="fas fa-trash"></i>
            </button>
        </td>
    `;
    
    console.log('Created new row:', newRow);
    tbody.insertBefore(newRow, totalRow);
    console.log('Row inserted successfully');
    
    // Add event listeners to new inputs
    const inputs = newRow.querySelectorAll('.editable-input');
    inputs.forEach(input => {
        input.addEventListener('change', function() {
            trackChange(this);
        });
        
        input.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                this.blur();
            }
        });
    });
    
    // Add event listener to new edit button
    const editBtn = newRow.querySelector('.edit-row-btn');
    editBtn.addEventListener('click', function() {
        editRow(this);
    });
    
    // Add event listener to new delete button
    const deleteBtn = newRow.querySelector('.delete-row-btn');
    deleteBtn.addEventListener('click', function() {
        deleteRow(this);
    });
    
    console.log('Event listeners added to new inputs');
}

function editRow(button) {
    const row = button.closest('tr');
    const type = button.getAttribute('data-type');
    const icon = button.querySelector('i');
    
    // Check if we're currently in edit mode
    const isEditing = row.classList.contains('editing');
    
    if (!isEditing) {
        // Enter edit mode
        row.classList.add('editing');
        
        // Show inputs, hide displays
        const categoryDisplay = row.querySelector('.category-display');
        const categoryInput = row.querySelector('.category-input');
        const amountDisplay = row.querySelector('.amount-display');
        const amountInput = row.querySelector('.amount-input');
        
        categoryDisplay.style.display = 'none';
        categoryInput.style.display = 'block';
        amountDisplay.style.display = 'none';
        amountInput.style.display = 'block';
        
        // Change icon to save
        icon.className = 'fas fa-save';
        button.title = 'Save';
        
        // Focus on category input
        categoryInput.focus();
        
    } else {
        // Save changes
        const categoryInput = row.querySelector('.category-input');
        const amountInput = row.querySelector('.amount-input');
        const categoryDisplay = row.querySelector('.category-display');
        const amountDisplay = row.querySelector('.amount-display');
        
        // Update displays with new values
        categoryDisplay.textContent = categoryInput.value;
        amountDisplay.textContent = '$' + parseFloat(amountInput.value || 0).toFixed(2);
        
        // Hide inputs, show displays
        categoryInput.style.display = 'none';
        categoryDisplay.style.display = 'inline';
        amountInput.style.display = 'none';
        amountDisplay.style.display = 'inline';
        
        // Remove edit mode
        row.classList.remove('editing');
        
        // Change icon back to pencil
        icon.className = 'fas fa-pencil-alt';
        button.title = 'Edit';
        
        // Track changes
        trackChange(categoryInput);
        trackChange(amountInput);
        
        // Recalculate differences
        recalculateDifferences(type);
    }
}

function deleteRow(button) {
    const row = button.closest('tr');
    if (confirm('Are you sure you want to delete this category?')) {
        row.remove();
        recalculateDifferences(row.querySelector('[data-type]').getAttribute('data-type'));
    }
}

function saveAllChanges(type) {
    const changes = type === 'income' ? incomeChanges : expenseChanges;
    
    if (changes.size === 0) {
        alert('No changes to save');
        return;
    }
    
    const saveBtn = document.querySelector(`.save-all-btn[data-type="${type}"]`);
    saveBtn.disabled = true;
    saveBtn.textContent = 'Saving...';
    
    // Convert changes to array format
    const changesArray = Array.from(changes.entries()).map(([index, changes]) => ({
        index: parseInt(index),
        ...changes
    }));
    
    fetch('/budget/update-categories', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: JSON.stringify({
            type: type,
            changes: changesArray
        })
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('Failed to save changes');
        }
    })
    .then(data => {
        if (data.success) {
            // Clear changes
            changes.clear();
            updateSaveButton(type);
            
            // Update original values
            const inputs = document.querySelectorAll(`[data-type="${type}"]`);
            inputs.forEach(input => {
                input.setAttribute('data-original', input.value);
            });
            
            saveBtn.textContent = 'Saved!';
            setTimeout(() => {
                saveBtn.disabled = false;
                saveBtn.textContent = 'Save All Changes';
            }, 2000);
            
            // Recalculate differences
            recalculateDifferences(type);
        } else {
            throw new Error(data.message || 'Failed to save changes');
        }
    })
    .catch(error => {
        console.error('Error saving changes:', error);
        saveBtn.textContent = 'Error!';
        saveBtn.style.background = '#dc3545';
        setTimeout(() => {
            saveBtn.disabled = false;
            saveBtn.textContent = 'Save All Changes';
            saveBtn.style.background = '#007bff';
        }, 2000);
    });
}

function recalculateDifferences(type) {
    const table = document.getElementById(`${type}-table`);
    const rows = table.querySelectorAll('.data-row');
    
    rows.forEach(row => {
        const plannedInput = row.querySelector('.amount-input');
        const actualCell = row.querySelector('td:nth-child(3)');
        const differenceCell = row.querySelector('td:nth-child(4)');
        
        const plannedAmount = parseFloat(plannedInput.value) || 0;
        const actualText = actualCell.textContent.replace('$', '').replace(',', '');
        const actualAmount = parseFloat(actualText) || 0;
        
        const difference = actualAmount - plannedAmount;
        
        differenceCell.textContent = '$' + difference.toFixed(2);
        differenceCell.className = 'readonly-cell amount-cell';
        
        if (difference > 0) {
            differenceCell.classList.add('amount-positive');
        } else if (difference < 0) {
            differenceCell.classList.add('amount-negative');
        } else {
            differenceCell.classList.add('amount-neutral');
        }
    });
    
    // Update totals
    updateTotals(type);
}

function updateTotals(type) {
    const table = document.getElementById(`${type}-table`);
    const rows = table.querySelectorAll('.data-row');
    const totalRow = table.querySelector('.table-total');
    
    let totalPlanned = 0;
    let totalActual = 0;
    
    rows.forEach(row => {
        const plannedInput = row.querySelector('.amount-input');
        const actualCell = row.querySelector('td:nth-child(3)');
        
        totalPlanned += parseFloat(plannedInput.value) || 0;
        const actualText = actualCell.textContent.replace('$', '').replace(',', '');
        totalActual += parseFloat(actualText) || 0;
    });
    
    const totalDifference = totalActual - totalPlanned;
    
    totalRow.querySelector('td:nth-child(2)').textContent = '$' + totalPlanned.toFixed(2);
    totalRow.querySelector('td:nth-child(3)').textContent = '$' + totalActual.toFixed(2);
    totalRow.querySelector('td:nth-child(4)').textContent = '$' + totalDifference.toFixed(2);
    
    // Update difference styling
    const differenceCell = totalRow.querySelector('td:nth-child(4)');
    differenceCell.className = 'amount-cell';
    if (totalDifference > 0) {
        differenceCell.classList.add('amount-positive');
    } else if (totalDifference < 0) {
        differenceCell.classList.add('amount-negative');
    } else {
        differenceCell.classList.add('amount-neutral');
    }
} 