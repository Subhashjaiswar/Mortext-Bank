import React, { useState } from 'react';
import './Chart.css';

const Chart = ({
  data = [], // [{ label: 'Jan', value: 300 }]
  type = 'area', // area, bar, line
  height = 220,
  color = 'var(--primary)',
  accentColor = 'var(--accent)',
  gridLines = true,
  className = '',
}) => {
  const [hoverIndex, setHoverIndex] = useState(null);

  if (!data || data.length === 0) {
    return (
      <div className="chart-empty" style={{ height }}>
        <p>No data available</p>
      </div>
    );
  }

  const values = data.map((d) => d.value);
  const maxVal = Math.max(...values, 1) * 1.15; // Give 15% headroom
  const minVal = Math.min(...values, 0);
  const valRange = maxVal - minVal;

  // Chart boundaries inside the SVG
  const paddingX = 40;
  const paddingY = 20;
  const svgWidth = 500;
  const svgHeight = height;

  const getCoordinates = () => {
    return data.map((d, index) => {
      const x = paddingX + (index / (data.length - 1)) * (svgWidth - paddingX * 2);
      // SVG 0,0 is at the top, so we invert the Y coordinate
      const normalizedY = (d.value - minVal) / valRange;
      const y = svgHeight - paddingY - normalizedY * (svgHeight - paddingY * 2);
      return { x, y, label: d.label, value: d.value };
    });
  };

  const coords = getCoordinates();

  // Create path for Line and Area
  const linePath = coords.reduce((path, coord, index) => {
    return index === 0 ? `M ${coord.x} ${coord.y}` : `${path} L ${coord.x} ${coord.y}`;
  }, '');

  // For Area, we close the shape at the bottom
  const areaPath = linePath
    ? `${linePath} L ${coords[coords.length - 1].x} ${svgHeight - paddingY} L ${coords[0].x} ${svgHeight - paddingY} Z`
    : '';

  // Render horizontal gridlines (typically 4 lines)
  const gridCount = 4;
  const gridLinesY = Array.from({ length: gridCount }).map((_, i) => {
    const ratio = i / (gridCount - 1);
    return svgHeight - paddingY - ratio * (svgHeight - paddingY * 2);
  });

  return (
    <div className={`chart-wrapper ${className}`}>
      <svg
        viewBox={`0 0 ${svgWidth} ${svgHeight}`}
        className="chart-svg"
        width="100%"
        height={height}
      >
        {/* Gradients */}
        <defs>
          <linearGradient id="areaGradient" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stopColor="var(--primary)" stopOpacity="0.3" />
            <stop offset="100%" stopColor="var(--primary)" stopOpacity="0.0" />
          </linearGradient>
          <linearGradient id="barGradient" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stopColor="var(--primary)" />
            <stop offset="100%" stopColor="var(--accent)" />
          </linearGradient>
        </defs>

        {/* Gridlines */}
        {gridLines &&
          gridLinesY.map((yVal, i) => (
            <line
              key={i}
              x1={paddingX}
              y1={yVal}
              x2={svgWidth - paddingX}
              y2={yVal}
              className="chart-gridline"
            />
          ))}

        {/* 1. AREA CHART */}
        {type === 'area' && areaPath && (
          <path d={areaPath} fill="url(#areaGradient)" className="chart-area-fill" />
        )}
        {type === 'area' && linePath && (
          <path
            d={linePath}
            fill="none"
            stroke="var(--primary)"
            strokeWidth="3"
            strokeLinecap="round"
            strokeLinejoin="round"
            className="chart-line-stroke"
          />
        )}

        {/* 2. LINE CHART */}
        {type === 'line' && linePath && (
          <path
            d={linePath}
            fill="none"
            stroke="var(--accent)"
            strokeWidth="3.5"
            strokeLinecap="round"
            strokeLinejoin="round"
            className="chart-line-stroke"
          />
        )}

        {/* Points / Interactivity for Area/Line */}
        {(type === 'area' || type === 'line') &&
          coords.map((coord, i) => (
            <g
              key={i}
              onMouseEnter={() => setHoverIndex(i)}
              onMouseLeave={() => setHoverIndex(null)}
              style={{ cursor: 'pointer' }}
            >
              {/* Invisible interactive overlay circle */}
              <circle cx={coord.x} cy={coord.y} r="15" fill="transparent" />
              {/* Actual data dot */}
              <circle
                cx={coord.x}
                cy={coord.y}
                r={hoverIndex === i ? '7' : '4'}
                fill={type === 'area' ? 'var(--primary)' : 'var(--accent)'}
                stroke="#ffffff"
                strokeWidth="2"
                className="chart-dot"
              />
            </g>
          ))}

        {/* 3. BAR CHART */}
        {type === 'bar' &&
          coords.map((coord, i) => {
            const barWidth = (svgWidth - paddingX * 2) / data.length * 0.55;
            const barX = coord.x - barWidth / 2;
            const barHeight = svgHeight - paddingY - coord.y;
            return (
              <g
                key={i}
                onMouseEnter={() => setHoverIndex(i)}
                onMouseLeave={() => setHoverIndex(null)}
                style={{ cursor: 'pointer' }}
              >
                {/* Rounded Top Bar */}
                <rect
                  x={barX}
                  y={coord.y}
                  width={barWidth}
                  height={barHeight}
                  rx="6"
                  ry="6"
                  fill="url(#barGradient)"
                  className={`chart-bar ${hoverIndex === i ? 'chart-bar-hover' : ''}`}
                />
              </g>
            );
          })}

        {/* X Axis Labels */}
        {coords.map((coord, i) => (
          <text
            key={i}
            x={coord.x}
            y={svgHeight - 4}
            className={`chart-axis-label ${hoverIndex === i ? 'chart-axis-label-active' : ''}`}
            textAnchor="middle"
          >
            {coord.label}
          </text>
        ))}

        {/* Y Axis Grid Indicators */}
        {gridLinesY.map((yVal, i) => {
          const ratio = i / (gridCount - 1);
          const gridVal = minVal + ratio * valRange;
          return (
            <text
              key={i}
              x={12}
              y={yVal + 4}
              className="chart-axis-label-y"
            >
              {gridVal >= 1000 ? `$${(gridVal / 1000).toFixed(1)}k` : `$${gridVal.toFixed(0)}`}
            </text>
          );
        })}
      </svg>

      {/* Floating Dynamic Tooltip inside Card */}
      {hoverIndex !== null && (
        <div
          className="chart-tooltip animate-scale-in"
          style={{
            position: 'absolute',
            left: `${(coords[hoverIndex].x / svgWidth) * 100}%`,
            top: `${(coords[hoverIndex].y / svgHeight) * 70}%`,
            transform: 'translateX(-50%) translateY(-100%)',
          }}
        >
          <div className="tooltip-label">{coords[hoverIndex].label}</div>
          <div className="tooltip-value">${coords[hoverIndex].value.toLocaleString()}</div>
        </div>
      )}
    </div>
  );
};

export default Chart;
