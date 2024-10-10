import React,{useState, useEffect} from 'react';
import Header from '../components/Header';
import { Box, Typography, styled, Button } from '@mui/material';
import AddInvoice from "../components/AddInvoice";
import Invoices from '../components/Invoices';
import { getAllInvoice, deleteInvoice } from '../services/api';
import {BrowserRouter as Router,Route,Routes,useNavigate} from 'react-router-dom';
import InvoicePage from '../pages/InvoicePage';
import axios from 'axios';
const Home = () => {
    const [addInvoice, setAddInvoice] = useState(false);
    const [invoices, setInvoices]= useState([]);

    useEffect(()=>{
        const getData = async()=>{
            try{
            const response = await getAllInvoice();  
            setInvoices(Array.isArray(response) ? response : []);  
           }catch(error){
            console.error('Error fetching invoices:', error);
                setInvoices([]);
           }
        };
        getData();
    },[]);

    const toggleInvoice = () => {
        setAddInvoice(true);
    }

    const removeInvoice = async (id) => {
        try{
        await deleteInvoice(id);

        const updatedInvoice = invoices.filter(invoice => invoice.id != id);
        setInvoices(updatedInvoice);
    }catch(error){
        console.error('Error deleting invoice:', error);
    }
    };

    const approveInvoice = async (id) => {
        try{
        await axios.patch(`http://localhost:8080/invoice/approve/${id}`);
        const updatedInvoices = invoices.map(invoice => 
            invoice.id === id ? { ...invoice, action: 'approved' } : invoice
        );
        setInvoices(updatedInvoices);
    }catch(error){
        console.error('Error approving invoice:',error);
    }
    };
    
    
    
    return (
        <Router>
            <Header />
            <Box style={{ margin: 20 }}>
                <Typography variant="h4">Pending Invoices</Typography>
                { !addInvoice && <Button 
                        variant="contained" 
                        onClick={() => toggleInvoice()}
                        style={{ marginTop: 15 }}
                    >Add Invoice</Button>
                }
                { addInvoice && <AddInvoice setAddInvoice={setAddInvoice} setInvoices={setInvoices}/>}  
                <Box>
                    <Invoices 
                    invoices={invoices}
                    removeInvoice={removeInvoice}
                    approveInvoice={approveInvoice}/>
                </Box>  
            </Box>

            <Routes>
            <Route path="/invoice/:id" element={<InvoicePage />} />
            </Routes>
        </Router>
    )
}

export default Home;