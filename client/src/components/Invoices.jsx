
import {Table, TableHead, TableBody, TableRow, TableCell, Button, styled, Typography} from '@mui/material';
import {approveInvoiceApi} from '../services/api';

const StyledTable = styled(Table)({
    width: '100%',
    margin: 20,
    marginTop: 40,
    '& > thead > tr > th':{
        background: '#000',
        color: '#FFFFFF',
        fontsize: 18
    },
    '& > tbody > tr > td':{
        fontsize: 16
    },
    '& > tbody >p':{
        fontsize: 18,
        marginTop: 15
    }
})
const Invoices=({invoices, removeInvoice, approveInvoice})=>{
    
    return(
        <StyledTable>
            <TableHead>
                <TableRow>
                    <TableCell>Vendor</TableCell>
                    <TableCell>Product</TableCell>
                    <TableCell>Amount</TableCell>
                    <TableCell>Email</TableCell>
                    <TableCell>Date</TableCell>
                    <TableCell>Status</TableCell>
                    <TableCell>Action</TableCell>
                    <TableCell>Invoice</TableCell>
                </TableRow>
            </TableHead>
            <TableBody>
                {
                invoices && Array.isArray(invoices) && invoices.length > 0 ?
                    invoices.map(invoice => (
                        <TableRow key={invoice.id}>
                            <TableCell>{invoice.vendor}</TableCell>
                            <TableCell>{invoice.product}</TableCell>
                            <TableCell>{invoice.amount}</TableCell>
                            <TableCell>{invoice.email}</TableCell>
                            <TableCell>{invoice.date}</TableCell>
                            <TableCell>{invoice.action}</TableCell>
                            <TableCell>
                            <Button
                                    variant="contained"
                                    color="success"
                                    onClick={() => approveInvoice(invoice.id)}
                                >
                                Approve
                                </Button> 
                            </TableCell>
                            <TableCell>
                                <Button
                                    variant="contained"
                                    onClick={() => window.open(`http://localhost:8080/invoice/pdf/${invoice.id}`, '_blank')}
                                >
                                Generate PDF
                                </Button>
                                <Button
                                    variant="contained" 
                                    color="error"
                                    onClick={()=> removeInvoice(invoice.id)}   >
                                🗑️
                                </Button>
                        </TableCell>
                        
                    </TableRow>
                ))
                :
                <Typography>No Pending Invoices.</Typography>
            }
            </TableBody>
        </StyledTable>
    )
}

export default Invoices;