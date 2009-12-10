import info.clearthought.layout.TableLayout;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;  


public class MainFrame extends JFrame {
	private static final long serialVersionUID = 2269971701250845501L;
	
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem openFileMenuItem;
	private JMenuItem exitMenuItem;

	private JTabbedPane tabs;
	
	private TwoDSample sample;
	private JTable sampleTable;
	private AbstractTableModel sampleTableModel;
    
	
	private XYSeriesCollection sampleGraphDataset;
	
	private JTable sampleParamsTable;
	private AbstractTableModel sampleParamsTableModel;

    private JTable corelationParamsTable;
    private AbstractTableModel corelationParamsTableModel;

    private LinearRegression linearRegression;
    private JTable linearRegressionParamsTable;
    private AbstractTableModel linearRegressionParamsTableModel;
    private XYSeriesCollection linearRegressionGraphDataset =  new XYSeriesCollection();
    private JLabel linearDetermination = new JLabel("Determination:");

    private NonLinearRegression nonLinearRegression;
    private JTable nonLinearRegressionParamsTable;
    private AbstractTableModel nonLinearRegressionParamsTableModel;
    private XYSeriesCollection nonLinearRegressionGraphDataset =  new XYSeriesCollection();
    private JLabel nonLinearDetermination = new JLabel("Determination:");


    public MainFrame(){
		setTitle("ASA - Lab 1 - Main Window");
		tabs = new JTabbedPane();
		
		//menu
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		openFileMenuItem = new JMenuItem("Open");
		openFileMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser openFileChooser = new JFileChooser();
				openFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				openFileChooser.setMultiSelectionEnabled(false);
				// TODO: убрать в релизе
				openFileChooser.setCurrentDirectory(new File("/Users/igorevsukov/Documents/DNU/ASA/data PSA/lab1/"));
				if (openFileChooser.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION){
					try {
						String fileName = openFileChooser.getSelectedFile().getAbsolutePath();
						BufferedReader input = new BufferedReader(new FileReader(fileName));
						try {
							String line = input.readLine();
							if (line == null) throw new Exception("Can't read data: file "+fileName+"is empty");
							MDObject first_obj = new MDObject(line);
							sample = new TwoDSample();//first_obj.getParams().length);
							sample.add(first_obj);
							while((line = input.readLine()) != null){
								try {
									sample.add(new MDObject(line));
								}catch (Exception ex) {
									System.out.println("can't add object to sample:" + ex.getMessage());
								}
							}
						}
						finally{
							input.close();
							sample.calculateParams();
							sampleTable.tableChanged(null);
							sampleParamsTable.tableChanged(null);
                            corelationParamsTable.tableChanged(null);
							setTitle("ASA - Lab 1 - " + fileName);
							
							refreshOriginalSampleGraphDataset();

                            linearRegression = new LinearRegression(sample);
                            linearRegressionParamsTable.tableChanged(null);
                            refreshLinearRegressionGraphDataset();
                            linearDetermination.setText(String.format("Determination: %3.0f",linearRegression.getDeterminationCoefficient()));

                            nonLinearRegression = new NonLinearRegression(sample);
                            nonLinearRegressionParamsTable.tableChanged(null);
                            refreshNonLinearRegressionGraphDataset();
                            nonLinearDetermination.setText(String.format("Determination: %3.0f",nonLinearRegression.getDeterminationCoefficient()));
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}

		});

		fileMenu.add(openFileMenuItem);
		fileMenu.addSeparator();
		
		exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		fileMenu.add(exitMenuItem);
		menuBar.add(fileMenu);
		
		setJMenuBar(menuBar);

        sampleTableModel = new AbstractTableModel(){
            private static final long serialVersionUID = 1L;

            @Override
            public int getColumnCount() {
                return sample == null ? 0 : sample.getDimension();
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return Double.class;
            }

            @Override
            public String getColumnName(int columnIndex) {
                return String.valueOf(columnIndex);
            }

            @Override
            public int getRowCount() {
                return sample == null ? 0 : sample.size();
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return sample.get(rowIndex).getParam(columnIndex);
            }

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
        };
        //original table
		sampleTable = new JTable(sampleTableModel);
		
		sampleParamsTableModel = new AbstractTableModel(){
			private static final long serialVersionUID = 1L;

			@Override
			public int getColumnCount() {
				if (sample == null) return 0;
				else return 3;
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 0) return String.class;
				else  return Double.class;
			}
			
			@Override
			public String getColumnName(int columnIndex) {
				switch (columnIndex) {
				case 0: return "";
				case 1: return "x";
				case 2: return "y";
				default: return String.valueOf(columnIndex);
				}

			}

			@Override
			public int getRowCount() {
				if (sample == null) return 0;
				else return 6;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
                if (columnIndex == 0) {
                    switch (rowIndex) {
                        case 0: return "Mean";
                        case 1: return "Dispersion";
                        case 2: return "Min";
                        case 3: return "Max";
                        case 4: return "Assymetry";
                        case 5: return "Excess";
                        default: return null;
                    }
                }
                else {
                    switch (rowIndex) {
                        case 0: return sample.getMean()[columnIndex-1];
                        case 1: return sample.getDispersion()[columnIndex-1];
                        case 2: return sample.getMin()[columnIndex-1];
                        case 3: return sample.getMax()[columnIndex-1];
                        case 4: return sample.getAssymentry()[columnIndex-1];
                        case 5: return sample.getExces()[columnIndex-1];
                        default: return null;
                    }
                }

			}
			
			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
		};
		sampleParamsTable = new JTable(sampleParamsTableModel);

        corelationParamsTableModel = new AbstractTableModel() {
			@Override
			public int getColumnCount() {
				if (sample == null) return 0;
				else return 6;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return String.class;
                    case 5: return String.class;
                    case 4: return Boolean.class;
                    default: return Double.class; 
                }
			}

			@Override
			public String getColumnName(int columnIndex) {
				switch (columnIndex) {
				case 0: return "Name";
				case 1: return "value";
				case 2: return "Staticstic";
				case 3: return "Quantile";
				case 4: return "Significant?";
				case 5: return "Confidient Limits";
                default: return "";
				}
			}

			@Override
			public int getRowCount() { return sample == null ? 0 : sample.getCorelationCoefficients().length; }
            
			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
                Criterion c = sample.getCorelationCoefficients()[rowIndex];
				switch (columnIndex) {
				case 0: return c.getName();
				case 1: return c.getValue();
				case 2: return c.getStatistic();
				case 3: return c.getQuantile();
				case 4: return c.isSignificant();
                case 5: return c.getConfidienceLimits();
				default: return null;
				}
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
        };
        corelationParamsTable = new JTable(corelationParamsTableModel);
        

		sampleGraphDataset =  new XYSeriesCollection();
		JFreeChart studyChart = ChartFactory.createScatterPlot("Sample", "", "", sampleGraphDataset, PlotOrientation.VERTICAL, false, true, false);
		ChartPanel studyChartPanel = new ChartPanel(studyChart);
		studyChartPanel.setVerticalAxisTrace(true);
		studyChartPanel.setHorizontalAxisTrace(true);
		
		
		JPanel samplePanel = new JPanel(new TableLayout(new double[][] {{0.3,20,0.7},{20,0.50,20,0.50}}));
        samplePanel.add(new JLabel("Sample"),"0, 0");
		samplePanel.add(new JScrollPane(sampleTable),"0, 1, 0, 3");
//		samplePanel.add(studyChartPanel,"1, 0");
        samplePanel.add(new JLabel("Sample Params"),"2, 0");		
		samplePanel.add(new JScrollPane(sampleParamsTable),"2, 1");
        samplePanel.add(new JLabel("Corelation Coefficients"),"2, 2");
        samplePanel.add(new JScrollPane(corelationParamsTable),"2, 3");
		tabs.add(samplePanel, "Sample",0);
		
//		tabs.add(new JScrollPane(sampleTable), "Original Sample", 0);

		tabs.add(studyChartPanel,"Graph",1);


        linearRegressionParamsTableModel = new AbstractTableModel() {
        	@Override
			public int getColumnCount() {
				if (linearRegression == null) return 0;
				else return 6;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return String.class;
                    case 5: return String.class;
                    case 4: return Boolean.class;
                    default: return Double.class;
                }
			}

			@Override
            public String getColumnName(int columnIndex) {
                switch (columnIndex) {
                    case 0: return "Name";
                    case 1: return "Value";
                    case 2: return "Staticstic";
                    case 3: return "Quantile";
                    case 4: return "Significant?";
                    case 5: return "Confidient Limits";
                    default: return "";
                }
            }

			@Override
			public int getRowCount() {
                return linearRegression == null ? 0 : linearRegression.getParams().length;
			}

			@Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                Criterion c = linearRegression.getParams()[rowIndex];
                switch (columnIndex) {
                    case 0: return c.getName();
                    case 1: return c.getValue();
                    case 2: return c.getStatistic();
                    case 3: return c.getQuantile();
                    case 4: return c.isSignificant();
                    case 5: return c.getConfidienceLimits();
                    default: return null;
                }
            }

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
        };
		linearRegressionParamsTable = new JTable(linearRegressionParamsTableModel);
        JPanel linearRegresssionPanel = new JPanel(new TableLayout(new double[][] {{TableLayout.FILL},{TableLayout.FILL,TableLayout.PREFERRED,100}}));
        linearRegresssionPanel.add(new JScrollPane(linearRegressionParamsTable), "0, 2");
        linearRegresssionPanel.add(linearDetermination,"0, 1");
        tabs.add(linearRegresssionPanel, "Linear Regression", 2);
//        tabs.add(new JScrollPane(linearRegressionParamsTable), "Linear Regression", 2);

        JFreeChart linearRegressionChart = ChartFactory.createScatterPlot("Linear Regression", "", "", linearRegressionGraphDataset, PlotOrientation.VERTICAL, true, true, false);
		ChartPanel linearRegressionChartPanel = new ChartPanel(linearRegressionChart);
//		studyChartPanel.setVerticalAxisTrace(true);
//		studyChartPanel.setHorizontalAxisTrace(true);
        XYLineAndShapeRenderer linearRegressionRenderer = new XYLineAndShapeRenderer();
        linearRegressionRenderer.setSeriesLinesVisible(0, false);
        linearRegressionRenderer.setSeriesShapesVisible(0, true);
        linearRegressionRenderer.setSeriesLinesVisible(1, true);
        linearRegressionRenderer.setSeriesShapesVisible(1, false);
        //confidience
        linearRegressionRenderer.setSeriesLinesVisible(2, true);
        linearRegressionRenderer.setSeriesShapesVisible(2, false);
        linearRegressionRenderer.setSeriesPaint(2, Color.green);
        linearRegressionRenderer.setSeriesLinesVisible(3, true);
        linearRegressionRenderer.setSeriesShapesVisible(3, false);
        linearRegressionRenderer.setSeriesPaint(3, Color.green);

        //new lookup confidience
        linearRegressionRenderer.setSeriesLinesVisible(4, true);
        linearRegressionRenderer.setSeriesShapesVisible(4, false);
        linearRegressionRenderer.setSeriesPaint(4, Color.blue);
        linearRegressionRenderer.setSeriesLinesVisible(5, true);
        linearRegressionRenderer.setSeriesShapesVisible(5, false);
        linearRegressionRenderer.setSeriesPaint(5, Color.blue);

        //tolerant
        linearRegressionRenderer.setSeriesLinesVisible(6, true);
        linearRegressionRenderer.setSeriesShapesVisible(6, false);
        linearRegressionRenderer.setSeriesPaint(6, Color.orange);
        linearRegressionRenderer.setSeriesLinesVisible(7, true);
        linearRegressionRenderer.setSeriesShapesVisible(7, false);
        linearRegressionRenderer.setSeriesPaint(7, Color.orange);

        linearRegressionChart.getXYPlot().setRenderer(linearRegressionRenderer);
        linearRegresssionPanel.add(linearRegressionChartPanel, "0, 0");


        // NON LINEAR
        nonLinearRegressionParamsTableModel = new AbstractTableModel() {
        	@Override
			public int getColumnCount() {
				if (nonLinearRegression == null) return 0;
				else return 6;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return String.class;
                    case 5: return String.class;
                    case 4: return Boolean.class;
                    default: return Double.class;
                }
			}

			@Override
            public String getColumnName(int columnIndex) {
                switch (columnIndex) {
                    case 0: return "Name";
                    case 1: return "Value";
                    case 2: return "Staticstic";
                    case 3: return "Quantile";
                    case 4: return "Significant?";
                    case 5: return "Confidient Limits";
                    default: return "";
                }
            }

			@Override
			public int getRowCount() {
                return nonLinearRegression == null ? 0 : nonLinearRegression.getParams().length;
			}

			@Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                Criterion c = nonLinearRegression.getParams()[rowIndex];
                switch (columnIndex) {
                    case 0: return c.getName();
                    case 1: return c.getValue();
                    case 2: return c.getStatistic();
                    case 3: return c.getQuantile();
                    case 4: return c.isSignificant();
                    case 5: return c.getConfidienceLimits();
                    default: return null;
                }
            }

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
        };
		nonLinearRegressionParamsTable = new JTable(nonLinearRegressionParamsTableModel);
        JPanel nonLinearRegresssionPanel = new JPanel(new TableLayout(new double[][] {{TableLayout.FILL},{TableLayout.FILL,TableLayout.PREFERRED,100}}));
        nonLinearRegresssionPanel.add(nonLinearDetermination, "0, 1");        
        nonLinearRegresssionPanel.add(new JScrollPane(nonLinearRegressionParamsTable), "0, 2");
        tabs.add(nonLinearRegresssionPanel, "Non-Linear Regression", 3);
        JFreeChart nonLinearRegressionChart = ChartFactory.createScatterPlot("Non-Linear Regression", "", "", nonLinearRegressionGraphDataset, PlotOrientation.VERTICAL, true, true, false);
		ChartPanel nonLinearRegressionChartPanel = new ChartPanel(nonLinearRegressionChart);
        XYLineAndShapeRenderer nonLinearRegressionRenderer = new XYLineAndShapeRenderer();
        nonLinearRegressionRenderer.setSeriesLinesVisible(0, false);
        nonLinearRegressionRenderer.setSeriesShapesVisible(0, true);
        nonLinearRegressionRenderer.setSeriesLinesVisible(1, true);
        nonLinearRegressionRenderer.setSeriesShapesVisible(1, false);
        //confidience
        nonLinearRegressionRenderer.setSeriesLinesVisible(2, true);
        nonLinearRegressionRenderer.setSeriesShapesVisible(2, false);
        nonLinearRegressionRenderer.setSeriesPaint(2, Color.green);
        nonLinearRegressionRenderer.setSeriesLinesVisible(3, true);
        nonLinearRegressionRenderer.setSeriesShapesVisible(3, false);
        nonLinearRegressionRenderer.setSeriesPaint(3, Color.green);
        //new lookup confidience
        nonLinearRegressionRenderer.setSeriesLinesVisible(4, true);
        nonLinearRegressionRenderer.setSeriesShapesVisible(4, false);
        nonLinearRegressionRenderer.setSeriesPaint(4, Color.blue);
        nonLinearRegressionRenderer.setSeriesLinesVisible(5, true);
        nonLinearRegressionRenderer.setSeriesShapesVisible(5, false);
        nonLinearRegressionRenderer.setSeriesPaint(5, Color.blue);
        //tolerant
        nonLinearRegressionRenderer.setSeriesLinesVisible(6, true);
        nonLinearRegressionRenderer.setSeriesShapesVisible(6, false);
        nonLinearRegressionRenderer.setSeriesPaint(6, Color.orange);
        nonLinearRegressionRenderer.setSeriesLinesVisible(7, true);
        nonLinearRegressionRenderer.setSeriesShapesVisible(7, false);
        nonLinearRegressionRenderer.setSeriesPaint(7, Color.orange);

        nonLinearRegressionChart.getXYPlot().setRenderer(nonLinearRegressionRenderer);
        nonLinearRegresssionPanel.add(nonLinearRegressionChartPanel, "0, 0");

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(tabs, BorderLayout.CENTER);
	}
	
	public void refreshOriginalSampleGraphDataset() {
		sampleGraphDataset.removeAllSeries();
		XYSeries series = new XYSeries("sample");
		for (int i = 0; i < sample.size(); i++) {
			series.add(sample.get(i).getParam(0), sample.get(i).getParam(1));
		}
		sampleGraphDataset.addSeries(series);
	}

    public void refreshLinearRegressionGraphDataset() {
        linearRegressionGraphDataset.removeAllSeries();
        XYSeries sampleSeries = new XYSeries("sample");
		for (int i = 0; i < sample.size(); i++)
			sampleSeries.add(sample.getParam(i,0), sample.getParam(i,1));
		linearRegressionGraphDataset.addSeries(sampleSeries);

        XYSeries regressionLine = new XYSeries("regression line");
        ArrayList<Point2D> regression =linearRegression.getRegressionLine();
        for (int i=0; i < regression.size(); i++) {
            Point2D p = regression.get(i);
            regressionLine.add(p.getX(),p.getY());
        }
        linearRegressionGraphDataset.addSeries(regressionLine);

        XYSeries topConfidienceSeries = new XYSeries("Top Confidience Limit");
        ArrayList<Point2D> topConfidience = linearRegression.getTopConfidienceLimit();
        for (int i=0; i < topConfidience.size(); i++) {
            Point2D p = topConfidience.get(i);
            topConfidienceSeries.add(p.getX(), p.getY());
        }
        linearRegressionGraphDataset.addSeries(topConfidienceSeries);
        XYSeries bottomConfidienceSeries = new XYSeries("Bottom Confidience Limit");
        ArrayList<Point2D> bottomConfidience = linearRegression.getBottomConfidienceLimit();
        for (int i=0; i < bottomConfidience.size(); i++) {
            Point2D p = bottomConfidience.get(i);
            bottomConfidienceSeries.add(p.getX(), p.getY());
        }
        linearRegressionGraphDataset.addSeries(bottomConfidienceSeries);

        XYSeries topNewConfidienceSeries = new XYSeries("Top New Lookup Confidience Limit");
        ArrayList<Point2D> topNewConfidience = linearRegression.getTopNewConfidienceLimit();
        for (int i=0; i < topNewConfidience.size(); i++) {
            Point2D p = topNewConfidience.get(i);
            topNewConfidienceSeries.add(p.getX(), p.getY());
        }
        linearRegressionGraphDataset.addSeries(topNewConfidienceSeries);
        XYSeries bottomNewConfidienceSeries = new XYSeries("Bottom New Lookup Confidience Limit");
        ArrayList<Point2D> bottomNewConfidience = linearRegression.getBottomNewConfidienceLimit();
        for (int i=0; i < bottomNewConfidience.size(); i++) {
            Point2D p = bottomNewConfidience.get(i);
            bottomNewConfidienceSeries.add(p.getX(), p.getY());
        }
        linearRegressionGraphDataset.addSeries(bottomNewConfidienceSeries);        

        XYSeries topTolerantSeries = new XYSeries("Top Tolerant Limit");
        ArrayList<Point2D> topTolerant = linearRegression.getTopTolerantLimit();
        for (int i=0; i < topTolerant.size(); i++) {
            Point2D p = topTolerant.get(i);
            topTolerantSeries.add(p.getX(), p.getY());
        }
        linearRegressionGraphDataset.addSeries(topTolerantSeries);
        XYSeries bottomTolerantSeries = new XYSeries("Bottom Tolerant Limit");
        ArrayList<Point2D> bottomTolerant = linearRegression.getBottomTolerantLimit();
        for (int i=0; i < bottomTolerant.size(); i++) {
            Point2D p = bottomTolerant.get(i);
            bottomTolerantSeries.add(p.getX(), p.getY());
        }
        linearRegressionGraphDataset.addSeries(bottomTolerantSeries);
    }

    public void refreshNonLinearRegressionGraphDataset() {
        nonLinearRegressionGraphDataset.removeAllSeries();
        XYSeries sampleSeries = new XYSeries("sample");
        for (int i = 0; i < sample.size(); i++)
			sampleSeries.add(sample.getParam(i,0), sample.getParam(i,1));
		nonLinearRegressionGraphDataset.addSeries(sampleSeries);

        XYSeries regressionLine = new XYSeries("regression line");
        ArrayList<Point2D> regression = nonLinearRegression.getRegressionLine();
        for (int i=0; i < regression.size(); i++) {
            Point2D p = regression.get(i);
            regressionLine.add(p.getX(),p.getY());
        }
        nonLinearRegressionGraphDataset.addSeries(regressionLine);

        XYSeries topConfidienceSeries = new XYSeries("Top Confidience Limit");
        ArrayList<Point2D> topConfidience = nonLinearRegression.getTopConfidienceLimit();
        for (int i=0; i < topConfidience.size(); i++) {
            Point2D p = topConfidience.get(i);
            topConfidienceSeries.add(p.getX(), p.getY());
        }
        nonLinearRegressionGraphDataset.addSeries(topConfidienceSeries);
        XYSeries bottomConfidienceSeries = new XYSeries("Bottom Confidience Limit");
        ArrayList<Point2D> bottomConfidience = nonLinearRegression.getBottomConfidienceLimit();
        for (int i=0; i < bottomConfidience.size(); i++) {
            Point2D p = bottomConfidience.get(i);
            bottomConfidienceSeries.add(p.getX(), p.getY());
        }
        nonLinearRegressionGraphDataset.addSeries(bottomConfidienceSeries);

        XYSeries topNewConfidienceSeries = new XYSeries("Top New Lookup Confidience Limit");
        ArrayList<Point2D> topNewConfidience = nonLinearRegression.getTopNewConfidienceLimit();
        for (int i=0; i < topNewConfidience.size(); i++) {
            Point2D p = topNewConfidience.get(i);
            topNewConfidienceSeries.add(p.getX(), p.getY());
        }
        nonLinearRegressionGraphDataset.addSeries(topNewConfidienceSeries);
        XYSeries bottomNewConfidienceSeries = new XYSeries("Bottom New Lookup Confidience Limit");
        ArrayList<Point2D> bottomNewConfidience = nonLinearRegression.getBottomNewConfidienceLimit();
        for (int i=0; i < bottomNewConfidience.size(); i++) {
            Point2D p = bottomNewConfidience.get(i);
            bottomNewConfidienceSeries.add(p.getX(), p.getY());
        }
        nonLinearRegressionGraphDataset.addSeries(bottomNewConfidienceSeries);

        XYSeries topTolerantSeries = new XYSeries("Top Tolerant Limit");
        ArrayList<Point2D> topTolerant = nonLinearRegression.getTopTolerantLimit();
        for (int i=0; i < topTolerant.size(); i++) {
            Point2D p = topTolerant.get(i);
            topTolerantSeries.add(p.getX(), p.getY());
        }
        nonLinearRegressionGraphDataset.addSeries(topTolerantSeries);
        XYSeries bottomTolerantSeries = new XYSeries("Bottom Tolerant Limit");
        ArrayList<Point2D> bottomTolerant = nonLinearRegression.getBottomTolerantLimit();
        for (int i=0; i < bottomTolerant.size(); i++) {
            Point2D p = bottomTolerant.get(i);
            bottomTolerantSeries.add(p.getX(), p.getY());
        }
        nonLinearRegressionGraphDataset.addSeries(bottomTolerantSeries);
    }
}
