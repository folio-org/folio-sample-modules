import React, { Component, PropTypes } from 'react';
import { Grid, Container, Row, Col, Form, FormGroup, FormControl, ControlLabel, Input, Button, ButtonGroup, Glyphicon } from 'react-bootstrap';
import {reduxForm} from 'redux-form';


// Patrons form for adding or editing patron data
// Uses redux-form (and older version) but that's expected to change.
//
// Not a 'connected' component, thus no manifest; this components gets
// all it needs from 'connected' parent components.
class PatronForm extends Component {

  static propTypes = {
    fields: PropTypes.object.isRequired,
    initializeForm: PropTypes.func.isRequired,
    handleSubmit: PropTypes.func.isRequired,
    cancelForm: PropTypes.func.isRequired,
    resetForm: PropTypes.func.isRequired,
    submitting: PropTypes.bool.isRequired
  };

  static actionTypes = {'create' : { title: 'Add Patron', submitLabel: 'Save Patron'},
                        'update' : { title: 'Edit Patron', submitLabel: 'Save Changes'}};

  render() {
    const {
      fields: {_id,patron_name,patron_code,
              contact_info,total_fines_paid,total_loans,status,patron_barcode,total_fines,patron_local_id},
      handleSubmit,
      cancelForm,
      resetForm,
      submitting,
      action
    } = this.props;
    return (
      <div>
        <Form inline>
          <h3>{action ? action.title : 'Patron'}</h3>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
              Name
            </Col>
            <Col sm={9}>
              <FormControl type='text' placeholder="Patron's name" {...patron_name} />
            </Col>
          </Row>
          <br/>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
              Status
            </Col>
            <Col sm={9}>
              <FormControl type='text' placeholder="Patron's status" {...status} />
            </Col>
          </Row>
          <br/>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
              Local Address
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              Line 1
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="First line of local address" {...contact_info.patron_address_local.line1} />
            </Col>
          </Row>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              Line 2
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="Second line" {...contact_info.patron_address_local.line2} />
            </Col>
          </Row>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              City
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="City" {...contact_info.patron_address_local.city} />
            </Col>
          </Row>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              State or province
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="State or province" {...contact_info.patron_address_local.state_province} />
            </Col>
          </Row>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              Postal code
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="Postal code" {...contact_info.patron_address_local.postal_code} />
            </Col>
          </Row>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              Note
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="Address note" {...contact_info.patron_address_local.address_note} />
            </Col>
          </Row>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              Start date
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="State date" {...contact_info.patron_address_local.start_date} />
            </Col>
          </Row>
          <br/>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
              Home Address
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              Line 1
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="First line of home address" {...contact_info.patron_address_home.line1} />
            </Col>
          </Row>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              Line 2
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="Second line" {...contact_info.patron_address_home.line2} />
            </Col>
          </Row>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              City
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="City" {...contact_info.patron_address_local.city} />
            </Col>
          </Row>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              State or province
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="State or province" {...contact_info.patron_address_home.state_province} />
            </Col>
          </Row>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              Postal code
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="Postal code" {...contact_info.patron_address_home.postal_code} />
            </Col>
          </Row>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              Note
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="Address note" {...contact_info.patron_address_home.address_note} />
            </Col>
          </Row>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              Start date
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="State date" {...contact_info.patron_address_home.start_date} />
            </Col>
          </Row>
          <br/>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
              Work Address
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              Line 1
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="First line of work address" {...contact_info.patron_address_work.line1} />
            </Col>
          </Row>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              Line 2
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="Second line" {...contact_info.patron_address_work.line2} />
            </Col>
          </Row>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              City
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="City" {...contact_info.patron_address_work.city} />
            </Col>
          </Row>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              State or province
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="State or province" {...contact_info.patron_address_work.state_province} />
            </Col>
          </Row>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              Postal code
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="Postal code" {...contact_info.patron_address_work.postal_code} />
            </Col>
          </Row>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              Note
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="Address note" {...contact_info.patron_address_work.address_note} />
            </Col>
          </Row>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              Start date
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="State date" {...contact_info.patron_address_work.start_date} />
            </Col>
          </Row>
          <br/>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
              Contact
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              Email
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="Patron's email" {...contact_info.patron_email} />
            </Col>
          </Row>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              Alternative email
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="Alternative email" {...contact_info.patron_email_alternative} />
            </Col>
          </Row>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              Cell phone
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="Cell phone #" {...contact_info.patron_phone_cell} />
            </Col>
          </Row>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              Home phone
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="Home phone #" {...contact_info.patron_home_phone} />
            </Col>
          </Row>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              Work phone
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="Work phone #" {...contact_info.patron_work_phone} />
            </Col>
          </Row>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
            </Col>
            <Col componentClass={ControlLabel} sm={3}>
              Primary contact
            </Col>
            <Col sm={6}>
              <FormControl type='text' placeholder="Primary contact info" {...contact_info.patron_primary_contact_info} />
            </Col>
          </Row>
          <br/>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
              Loans
            </Col>
            <Col sm={9}>
              <FormControl type='text' placeholder="Total loans" {...total_loans} />
            </Col>
          </Row>
          <br/>
          <Row>
          <Col componentClass={ControlLabel} sm={3}>
              Fines
            </Col>
            <Col sm={9}>
              <FormControl type='text' placeholder="Total fines" {...total_fines} />
            </Col>
          </Row>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
              Fines paid
            </Col>
            <Col sm={9}>
              <FormControl type='text' placeholder="Total fines paid" {...total_fines_paid} />
            </Col>
          </Row>
          <br/>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
              Barcode
            </Col>
            <Col sm={9}>
              <FormControl type='text' placeholder="Patron's barcode" {...patron_barcode} />
            </Col>
          </Row>
          <br/>
          <Row>
            <Col componentClass={ControlLabel} sm={3}>
              Local ID
            </Col>
            <Col sm={9}>
              <FormControl type='text' placeholder="Patron's local ID" {...patron_local_id} />
            </Col>
          </Row>
          <br/>

          <Row>
            <Col componentClass={ControlLabel} sm={3}>
              Patron code
            </Col>
            <Col sm={3}>
              <FormControl type='text' placeholder="Code" {...patron_code.value} />
            </Col>
            <Col sm={3}>
              <FormControl type='text' placeholder="Description" {...patron_code.description} />
            </Col>
          </Row>
          <br/>
          <ButtonGroup>
            <Button bsStyle='primary' disabled={submitting} onClick={handleSubmit}>{action ? action.submitLabel : 'Save'}</Button>
            <Button disabled={submitting} onClick={resetForm}>Reset</Button>
            <Button disabled={submitting} onClick={cancelForm}>Cancel</Button>
          </ButtonGroup>
        </Form>
        <br/>
        <br/>
      </div>
    );
  }
}

export default reduxForm(
  {
    form: 'patronForm',
    fields: ['_id','patron_name',
             'patron_code.value', 'patron_code.description',
             'contact_info.patron_address_local.line1',
               'contact_info.patron_address_local.line2',
               'contact_info.patron_address_local.city',
               'contact_info.patron_address_local.state_province',
               'contact_info.patron_address_local.postal_code',
               'contact_info.patron_address_local.address_note',
               'contact_info.patron_address_local.start_date',
             'contact_info.patron_address_home.line1',
               'contact_info.patron_address_home.line2',
               'contact_info.patron_address_home.city',
               'contact_info.patron_address_home.state_province',
               'contact_info.patron_address_home.postal_code',
               'contact_info.patron_address_home.address_note',
               'contact_info.patron_address_home.start_date',
             'contact_info.patron_address_work.line1',
               'contact_info.patron_address_work.line2',
               'contact_info.patron_address_work.city',
               'contact_info.patron_address_wokr.state_province',
               'contact_info.patron_address_work.postal_code',
               'contact_info.patron_address_work.address_note',
               'contact_info.patron_address_work.start_date',
             'total_fines_paid','total_loans','status','patron_barcode','total_fines','patron_local_id']
  }
)(PatronForm);

